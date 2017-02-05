package edu.usf.ratsim.nsl.modules.rl;

import java.util.LinkedList;
import java.util.ListIterator;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * This class implements the Actor Critic learning algorithm over multiple
 * states (e.g. place cell output).
 * 
 * It uses a 2D port as its main data source, were it keeps the action-value for
 * each action-state combination. This array also stores the value for each
 * state in the last column of each row.
 * 
 * Instead of using eligibility traces, this model implements replay events in a
 * high level approach. The update information is saved in each iteration and
 * the whole sequence is replayed upon request.
 * 
 * Finally, for each active state, the state value estimation is updated using
 * the actor critic update equation.
 * 
 * @author Martin Llofriu
 *
 */
public class MultiStateACReplay extends Module implements QLAlgorithm {

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

	private LinkedList<UpdateItem> path;

	private int numReplay;

	/**
	 * Create the multiscale actor critic
	 * 
	 * @param name
	 *            The module's name
	 * @param numActions
	 *            The number of possible actions
	 * @param numStates
	 *            The number of possible states
	 * @param rlDiscountFactor
	 *            The RL discount factor
	 * @param alpha
	 *            The learning rate
	 */
	public MultiStateACReplay(String name, int numActions, int numStates, float rlDiscountFactor, float alpha, int numReplay) {
		super(name);
		this.alpha = alpha;
		this.rlDiscountFactor = rlDiscountFactor;

		this.numActions = numActions;
		
		this.numReplay = numReplay;
		
		path = new LinkedList<UpdateItem>();
		
		update = true;
	}

	/**
	 * All input ports are obtained, eligibility traces computed and then each
	 * active state is updated.
	 * 
	 * @param reward
	 *            Represents the last reward obtained
	 * @param takenAction
	 *            The last take action index
	 * @param statesBefore
	 *            The activation value for all states before movement
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
			Float1dPort rlValueEstBefore = (Float1dPort) getInPort("rlValueEstimationBefore");
			Float1dPort rlValueEstAfter = (Float1dPort) getInPort("rlValueEstimationAfter");
			FloatMatrixPort value = (FloatMatrixPort) getInPort("value");

			// Gets the active state as computed at the beginning of the cycle
			int a = takenAction.get();

			if (Debug.printValueAfter)
				System.out.println("Value after: " + rlValueEstAfter.get(0));

			UpdateItem ui = new UpdateItem(statesBefore.getNonZero(), a, reward.get(), rlValueEstBefore.get(0),
					rlValueEstAfter.get(0));
			
			path.add(ui);

			update(ui, value, true);
			
			if (reward.get() > 0)
				for (int i = 0; i < numReplay; i++)
					replay();
		}
	}

	/**
	 * Updates the actor critic value estimation and action value based on the
	 * last update
	 * 
	 * @param ui
	 *            Item containing all the needed information to update: state
	 *            activation, executed action, reward, and value estimation
	 *            before and after the action
	 * @param value The action-value and value holding matrix
	 * @param updateValue 
	 */
	private void update(UpdateItem ui, FloatMatrixPort value, boolean updateValue) {
		for (Integer state : ui.states.keySet()) {
			float valueDelta = ui.reward + rlDiscountFactor * ui.valueEstAfter - ui.valueEstBefore;
			float activation = ui.states.get(state);;
			
			// Update value
			if (updateValue){
				float currValue = value.get(state, numActions);
				float newValue = Math.min(1000, Math.max(-1000,currValue + alpha * activation * valueDelta));
				if (Float.isInfinite(newValue) || Float.isNaN(newValue)) {
					System.out.println("Numeric Error");
					System.exit(1);
				}
				value.set(state, numActions, newValue);
			}

			// Update action value
			if (ui.action != -1){
				float actionDelta = ui.reward + rlDiscountFactor * ui.valueEstAfter - ui.valueEstBefore;
				float actionVal = value.get(state, ui.action);
				float newActionValue = actionVal + alpha * activation * (actionDelta);
	
				if (Debug.printDelta)
					System.out.println("State: " + (float) state / ui.states.size() + " V Delta: " + valueDelta
							+ " A Delta: " + actionDelta);
	
				if (Float.isInfinite(newActionValue) || Float.isNaN(newActionValue)) {
					System.out.println("Numeric Error");
					System.exit(1);
				}
				value.set(state, ui.action, newActionValue);
			}
		}
	}
	
	public void replay(){
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
//		Collections.reverse(path);
		ListIterator<UpdateItem> iter = path.listIterator(Math.max(0, path.size() - 50));
		while (iter.hasNext())
			update(iter.next(), value, false);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void newEpisode() {
	}

	public void clearPath() {
		path.clear();
	}

}