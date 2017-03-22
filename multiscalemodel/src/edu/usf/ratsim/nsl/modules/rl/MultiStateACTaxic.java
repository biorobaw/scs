package edu.usf.ratsim.nsl.modules.rl;

import java.util.HashSet;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

/**
 * This class implements the Actor Critic learning algorithm over multiple
 * states (e.g. place cell output).
 * 
 * It uses a 2D port as its main data source, were it keeps the action-value for
 * each action-state combination. This array also stores the value for each
 * state in the last column of each row.
 * 
 * Eligibility traces are also implemented. A set of active states is kept to
 * avoid iterating over all possible states. A sparse port is also used for the
 * state activation. Then, the only the non-zero states are added to the set of
 * active states in each cycle.
 * 
 * All active states and state-action combination are decreased by a factor of
 * tracesDecay. The activeStates that reach a 0 value after the decrement are
 * taken out of the list.
 * 
 * Finally, for each active state, the state value estimation is updated using
 * the actor critic update equation.
 * 
 * Besides comparing the reward + estimated value vs previous estimated value,
 * value information from other moduels is considered as well. A "taxic" value
 * estimation for the before and after states is inputed to the algorithm. This
 * corresponds to the value estimated from sensorial information, such as
 * observing a feeder or a flashing light. By incorporating taxic information,
 * negative outcomes can be learned due to the prescence of a expected return
 * derived from the visual scene.
 * 
 * @author Martin Llofriu
 *
 */
public class MultiStateACTaxic extends Module implements QLAlgorithm {

	/**
	 * The minimum value to consider a state no longer active
	 */
	private static final float EPS = .01f;

	/**
	 * Learning rate
	 */
	private float alpha;

	/**
	 * Whether to apply the update or not in each cycle
	 */
	private boolean update;

	/**
	 * The number of possible actions
	 */
	private int numActions;

	/**
	 * The discount factor for the RL (non-taxic) part of the equation
	 */
	private float rlDiscountFactor;

	/**
	 * Discount factor for the taxic part of the equation
	 */
	private float taxicDiscountFactor;

	/**
	 * The rate of decay for eligibility traces. O means no eligibility traces
	 * use.
	 */
	private float tracesDecay;

	/**
	 * The eligibility traces for states
	 */
	private float[] stateTraces;

	/**
	 * Eligibility traces for action-state combinations
	 */
	private float[][] actionTraces;

	/**
	 * The set of active states
	 */
	private HashSet<Integer> activeStates;

	/**
	 * States to be removed from the active states. TODO: remove as a field?
	 */
	private HashSet<Integer> toRemove;

	/**
	 * Create the multiscale actor critic
	 * 
	 * @param name
	 *            The module's name
	 * @param numActions
	 *            The number of possible actions
	 * @param numStates
	 *            The number of possible states
	 * @param taxicDiscountFactor
	 *            The taxic discount factor
	 * @param rlDiscountFactor
	 *            The RL discount factor
	 * @param alpha
	 *            The learning rate
	 * @param tracesDecay
	 *            The rate of decay for eligibility traces
	 */
	public MultiStateACTaxic(String name, int numActions, int numStates, float taxicDiscountFactor,
			float rlDiscountFactor, float alpha, float tracesDecay) {
		super(name);
		this.alpha = alpha;
		this.rlDiscountFactor = rlDiscountFactor;
		this.tracesDecay = tracesDecay;
		if (tracesDecay != 0) {
			stateTraces = new float[numStates];
			actionTraces = new float[numStates][numActions];
		}
		this.taxicDiscountFactor = taxicDiscountFactor;

		this.numActions = numActions;

		update = true;

		activeStates = new HashSet<Integer>(10000);
		toRemove = new HashSet<Integer>(100);
	}

	/**
	 * All input ports are obtained, eligibility traces computed and then each active state is updated.
	 * 
	 * @param reward
	 *            Represents the last reward obtained
	 * @param takenAction
	 *            The last take action index
	 * @param statesBefore
	 *            The activation value for all states before movement
	 * @param taxicValueEstAfter
	 *            The taxic value estimation of the location reached before
	 *            movement. See modules TaxicValueSchema and
	 *            FlashingTaxicValueSchema in actionselection.taxic
	 * @param taxicValueEstAfter
	 *            The taxic value estimation of the location reached after
	 *            movement. See modules TaxicValueSchema and
	 *            FlashingTaxicValueSchema in actionselection.taxic
	 * @param rlValueEstBefore
	 *            The value estimation of the location reached before movement
	 *            using the value table. See modules ProportionalValue
	 *            HalfAndHalfConnectionValue and GradientValue in action
	 *            selection
	 * @param value
	 *            The value table. Each row corresponds to a state, each column
	 *            to an action. it contains action-state values (q table) and
	 *            the state value function V (last column).
	 */
	public void run() {
		// Updates may be disabled for data log reasons
		if (update) {
			Float0dPort reward = (Float0dPort) getInPort("reward");
			Int0dPort takenAction = (Int0dPort) getInPort("takenAction");
			Float1dSparsePort statesBefore = (Float1dSparsePort) getInPort("statesBefore");
			Float1dPort taxicValueEstBefore = (Float1dPort) getInPort("taxicValueEstimationBefore");
			Float1dPort taxicValueEstAfter = (Float1dPort) getInPort("taxicValueEstimationAfter");
			Float1dPort rlValueEstBefore = (Float1dPort) getInPort("rlValueEstimationBefore");
			Float1dPort rlValueEstAfter = (Float1dPort) getInPort("rlValueEstimationAfter");
			Float2dPort value = (Float2dPort) getInPort("value");
			// Gets the active state as computed at the beginning of the cycle
			int a = takenAction.get();

			if (Debug.printValueAfter)
				System.out.println("Value after: " + rlValueEstAfter.get(0));

			activeStates.addAll(statesBefore.getNonZero().keySet());
			toRemove.clear();
			for (Integer state : activeStates) {
				boolean stillActive = false;
				stateTraces[state] *= tracesDecay;
				// Replacing states
				if (statesBefore.getNonZero().containsKey(state))
					stateTraces[state] = Math.max(statesBefore.get(state), stateTraces[state]);
				stillActive = stillActive || stateTraces[state] > EPS;
				for (int i = 0; i < numActions; i++) {
					actionTraces[state][i] *= tracesDecay;
					stillActive = stillActive || actionTraces[state][i] > EPS;
				}
				if (a != -1 && statesBefore.getNonZero().containsKey(state)) {
					actionTraces[state][a] = Math.max(statesBefore.get(state), actionTraces[state][a]);
					stillActive = stillActive || actionTraces[state][a] > EPS;
				}

				if (stillActive && a != -1)
					updateLastAction(state, a, statesBefore, value, reward, taxicValueEstBefore, taxicValueEstAfter,
							rlValueEstBefore, rlValueEstAfter, stateTraces, actionTraces);
				else if (!stillActive)
					toRemove.add(state);
			}
			activeStates.removeAll(toRemove);
		}
	}

	/**
	 * Updates a given active state
	 * 
	 * @param sBefore The index of the state
	 * @param a The taken action
	 * @param statesBefore The set of state activations before movement
	 * @param value The value table
	 * @param reward The last obtained reward
	 * @param taxicValueEstBefore The taxic value estimation before 
	 * @param taxicValueEstAfter The taxic value estimation after
	 * @param rlValueEstBefore The rl value estimation before
	 * @param rlValueEstAfter The rl value estimation after
	 * @param stateTraces The eligibility traces activation for the states
	 * @param actionTraces The eligiblity traces activation for the state-action pairs
	 */ 
	private void updateLastAction(int sBefore, int a, Float1dSparsePort statesBefore, Float2dPort value,
			Float0dPort reward, Float1dPort taxicValueEstBefore, Float1dPort taxicValueEstAfter,
			Float1dPort rlValueEstBefore, Float1dPort rlValueEstAfter, float[] stateTraces, float[][] actionTraces) {
		// Error in estimation
		// float delta = reward.get() + lambda * valueEstAfter.get(0)
		// - valueEstBefore.get(0);

		float valueDelta = reward.get() + taxicDiscountFactor * taxicValueEstAfter.get(0)
				+ rlDiscountFactor * rlValueEstAfter.get(0) - (taxicValueEstBefore.get(0) + rlValueEstBefore.get(0));

		// Update value
		float currValue = value.get(sBefore, numActions);
		float activation;
		if (tracesDecay == 0)
			activation = statesBefore.get(sBefore);
		else
			activation = stateTraces[sBefore];
		float newValue = currValue + alpha * activation * valueDelta;
		if (Float.isInfinite(newValue) || Float.isNaN(newValue)) {
			System.out.println("Numeric Error");
			System.exit(1);
		}
		value.set(sBefore, numActions, newValue);

		for (int updateAction = 0; updateAction < numActions; updateAction++) {
			float actionDelta = reward.get() + taxicDiscountFactor * taxicValueEstAfter.get(0)
					+ rlDiscountFactor * rlValueEstAfter.get(0)
					- (taxicValueEstBefore.get(0) + rlValueEstBefore.get(0));
			float actionVal = value.get(sBefore, updateAction);
			if (tracesDecay == 0)
				activation = statesBefore.get(sBefore);
			else
				activation = actionTraces[sBefore][updateAction];
			float newActionValue = actionVal + alpha * activation * (actionDelta);

			if (Debug.printDelta)
				System.out.println("State: " + (float) sBefore / statesBefore.getSize() + " V Delta: " + valueDelta
						+ " A Delta: " + actionDelta);

			if (Float.isInfinite(newActionValue) || Float.isNaN(newActionValue)) {
				System.out.println("Numeric Error");
				System.exit(1);
			}
			value.set(sBefore, updateAction, newActionValue);
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void newEpisode() {
		for (Integer state : activeStates) {
			stateTraces[state] = 0;
			// Replacing states
			for (int i = 0; i < numActions; i++) {
				actionTraces[state][i] = 0;
			}
		}
		activeStates.clear();
	}

}