package edu.usf.ratsim.nsl.modules.qlearning.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Float1dPort;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Float1dSparsePort;
import edu.usf.ratsim.micronsl.FloatMatrixPort;
import edu.usf.ratsim.micronsl.Int1dPort;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.support.Configuration;

public class MultiStateProportionalAC extends Module implements QLAlgorithm {

	private static final String DUMP_FILENAME = "policy.txt";

	private static final float EPS = .01f;

	private static PrintWriter writer;

	private float alpha;
	private float discountFactor;

	private boolean update;

	private Subject subject;

	private int numActions;

	private float rlDiscountFactor;

	private float taxicDiscountFactor;

	private float tracesDecay;

	private float[] stateTraces;

	private float[][] actionTraces;

	private HashSet<Integer> activeStates;

	private HashSet<Integer> toRemove;

	public MultiStateProportionalAC(String name, Subject subject,
			int numActions, int numStates, float taxicDiscountFactor,
			float rlDiscountFactor, float alpha, float tracesDecay,
			float initialValue) {
		super(name);
		this.alpha = alpha;
		this.rlDiscountFactor = rlDiscountFactor;
		this.tracesDecay = tracesDecay;
		if (tracesDecay != 0) {
			stateTraces = new float[numStates];
			actionTraces = new float[numStates][numActions];
		}
		this.taxicDiscountFactor = taxicDiscountFactor;
		this.subject = subject;

		this.numActions = numActions;

		update = true;

		activeStates = new HashSet<Integer>(10000);
		toRemove = new HashSet<Integer>(100);
	}

	public void run() {
		// Updates may be disabled for data log reasons
		if (update) {
			Float1dPortArray reward = (Float1dPortArray) getInPort("reward");
			Int1dPort takenAction = (Int1dPort) getInPort("takenAction");
			Float1dSparsePort statesBefore = (Float1dSparsePort) getInPort("statesBefore");
			Float1dPort statesAfter = (Float1dPort) getInPort("statesAfter");
			Float1dPort taxicValueEstBefore = (Float1dPort) getInPort("taxicValueEstimationBefore");
			Float1dPort taxicValueEstAfter = (Float1dPort) getInPort("taxicValueEstimationAfter");
			Float1dPort rlValueEstBefore = (Float1dPort) getInPort("rlValueEstimationBefore");
			Float1dPort rlValueEstAfter = (Float1dPort) getInPort("rlValueEstimationAfter");
			FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
			// Gets the active state as computed at the beginning of the cycle
			int a = takenAction.get();

			if (Debug.printValueAfter)
				System.out.println("Value after: " + rlValueEstAfter.get(0));

			activeStates.addAll(statesBefore.getNonZero()
					.keySet());
			toRemove.clear();
			for (Integer state : activeStates) {
				boolean stillActive = false;
				stateTraces[state] *= tracesDecay;
				// Replacing states
				if (statesBefore.getNonZero().containsKey(state))
					stateTraces[state] = Math
							.max(statesBefore.get(state),
									stateTraces[state]);
				stillActive = stillActive || stateTraces[state] > EPS;
				for (int i = 0; i < numActions; i++) {
					actionTraces[state][i] *= tracesDecay;
					stillActive = stillActive || actionTraces[state][i] > EPS;
				}
				if (a != -1 && statesBefore.getNonZero().containsKey(state)) {
					actionTraces[state][a] = Math.max(
							statesBefore.get(state),
							actionTraces[state][a]);
					stillActive = stillActive || actionTraces[state][a] > EPS;
				}

				if (stillActive && a != -1)
					updateLastAction(state, a, statesBefore, value,
							reward, taxicValueEstBefore, taxicValueEstAfter,
							rlValueEstBefore, rlValueEstAfter, stateTraces,
							actionTraces);
				else if (!stillActive)
					toRemove.add(state);
			}
			activeStates.removeAll(toRemove);
		}
	}

	private void updateLastAction(int sBefore, int a, Float1dSparsePort statesBefore,
			FloatMatrixPort value, Float1dPort reward,
			Float1dPort taxicValueEstBefore, Float1dPort taxicValueEstAfter,
			Float1dPort rlValueEstBefore, Float1dPort rlValueEstAfter,
			float[] stateTraces, float[][] actionTraces) {
		// Error in estimation
		// float delta = reward.get() + lambda * valueEstAfter.get(0)
		// - valueEstBefore.get(0);

		float valueDelta = reward.get() + taxicDiscountFactor
				* taxicValueEstAfter.get(0) + rlDiscountFactor
				* rlValueEstAfter.get(0)
				- (taxicValueEstBefore.get(0) + rlValueEstBefore.get(0));

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
			float actionDelta = reward.get() + taxicDiscountFactor
					* taxicValueEstAfter.get(0) + rlDiscountFactor
					* rlValueEstAfter.get(0)
					- (taxicValueEstBefore.get(0) + rlValueEstBefore.get(0));
			float actionVal = value.get(sBefore, updateAction);
			if (tracesDecay == 0)
				activation = statesBefore.get(sBefore);
			else
				activation = actionTraces[sBefore][updateAction];
			float newActionValue = actionVal + alpha * activation
					* (actionDelta);

			if (Debug.printDelta)
				System.out.println("State: " + (float) sBefore
						/ statesBefore.getSize() + " V Delta: " + valueDelta
						+ " A Delta: " + actionDelta);

			if (Float.isInfinite(newActionValue) || Float.isNaN(newActionValue)) {
				System.out.println("Numeric Error");
				System.exit(1);
			}
			value.set(sBefore, updateAction, newActionValue);
		}

		// } else {
		// // value.set(sBefore, numActions, -3);
		// if (Debug.printSilentSynapses)
		// System.out.println("Not updating because synapse is silent");
		// }
	}

	/**
	 * Dumps the qlearning policy with a certain intention to a file. The
	 * alignment between pcl cells and ql states is assumed for efficiency
	 * purposes.
	 * 
	 * @param rep
	 * @param subName
	 * @param trial
	 * @param rep
	 * 
	 * @param writer
	 * @param pcl
	 */
	public void dumpPolicy(String trial, String groupName, String subName,
			String rep, int numIntentions, Universe univ, Subject sub) {
		// TODO: get dumppolicy back
		// synchronized (MultiStateProportionalQL.class) {
		// // Deactivate updates
		// sub.setPassiveMode(true);
		// PrintWriter writer = MultiStateProportionalQL.getWriter();
		//
		// for (int intention = 0; intention < numIntentions; intention++) {
		// for (float xInc = MARGIN; xInc
		// - (univ.getBoundingRectangle().getWidth() - MARGIN / 2) < 1e-8; xInc
		// += INTERVAL) {
		// for (float yInc = MARGIN; yInc
		// - (univ.getBoundingRectangle().getHeight() - MARGIN / 2) < 1e-8; yInc
		// += INTERVAL) {
		// float x = (float) (univ.getBoundingRectangle()
		// .getMinX() + xInc);
		// float y = (float) (univ.getBoundingRectangle()
		// .getMinY() + yInc);
		//
		// // List<Float> preferredAngles = new
		// // LinkedList<Float>();
		// float maxVal = Float.NEGATIVE_INFINITY;
		// float bestAngle = 0;
		// for (float angle = 0; angle <= 2 * Math.PI; angle += ANGLE_INTERVAL)
		// {
		// univ.setRobotPosition(new Point2D.Float(x, y),
		// angle);
		// rat.stepCycle();
		// // // float forwardVal =
		// // ((MultiScaleMultiIntentionCooperativeModel) rat
		// // //
		// //
		// .getModel()).getQLVotes().getVotes().get(Utiles.discretizeAction(0));
		// // if( forwardVal > maxVal){
		// // maxVal = forwardVal;
		// // bestAngle = angle;
		// // }
		// for (int action = 0; action < subject.getNumActions(); action++) {
		// float angleVal = ((MultiScaleMultiIntentionCooperativeModel) rat
		// .getModel()).getQLVotes().getVotes()
		// .get(action);
		// if (angleVal > maxVal) {
		// maxVal = angleVal;
		// bestAngle = angle;
		// }
		// }
		//
		// // If goes forward, it is the preferred angle
		// }
		//
		// String preferredAngleString = new Float(bestAngle)
		// .toString();
		//
		// writer.println(trial + '\t' + groupName + '\t'
		// + subName + '\t' + rep + '\t' + x + "\t" + y
		// + "\t" + intention + "\t"
		// + preferredAngleString + "\t" + maxVal);
		//
		// }
		// }
		// }
		// // Re enable updates
		// ((RLRatModel) rat.getModel()).setPassiveMode(false);
		// univ.clearRobotAte();
		//
		// }
	}

	private static PrintWriter getWriter() {
		if (writer == null) {
			try {
				writer = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(new File(Configuration
								.getString("Log.DIRECTORY") + DUMP_FILENAME))),
						true);
				writer.println("trial\tgroup\tsubject\trepetition\tx\ty\tintention\theading\tval");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return writer;
	}

	public void setUpdatesEnabled(boolean enabled) {
		update = enabled;
	}

	@Override
	public void savePolicy() {
		// FileOutputStream fout;
		// try {
		// fout = new FileOutputStream("policy.obj");
		// ObjectOutputStream oos = new ObjectOutputStream(fout);
		// oos.writeObject(value.getData());
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}