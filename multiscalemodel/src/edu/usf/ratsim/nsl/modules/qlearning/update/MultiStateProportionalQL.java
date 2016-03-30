package edu.usf.ratsim.nsl.modules.qlearning.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.FloatMatrixPort;
import edu.usf.micronsl.Int1dPort;
import edu.usf.micronsl.Module;
import edu.usf.ratsim.support.Configuration;

public class MultiStateProportionalQL extends Module implements QLAlgorithm {

	private static final String DUMP_FILENAME = "policy.txt";

	private static final float EPS = 0.2f;

	private static final float INTERVAL = 0.05f;
	// Margin for ignoring inside maze
	private static final float MARGIN = 0.1f;

	private static final float ANGLE_INTERVAL = 0.314f;

	private static PrintWriter writer;

	private float alpha;
	private float discountFactor;
	private int numStates;

	private boolean update;

	private Subject subject;

	public MultiStateProportionalQL(String name, Subject subject,
			int numActions, float taxicDiscountFactor, float rlDiscountFactor, float alpha,
			float initialValue) {
		super(name);

		// TODO: fix discount factor thing 
		
		this.alpha = alpha;
		this.subject = subject;

		// File f = new File("policy.obj");
		// if (f.exists()
		// && Configuration.getBoolean("Experiment.loadSavedPolicy")) {
		//
		// try {
		// System.out.println("Reading saved policy...");
		// FileInputStream fin;
		// fin = new FileInputStream(f);
		// ObjectInputStream ois = new ObjectInputStream(fin);
		// value.set((float[][]) ois.readObject());
		// } catch (FileNotFoundException e) {
		// value.set(initialValue);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// value.set(initialValue);
		// }

		// for (int s = 0; s < numStates; s++)
		// for (int a = 0; a < numActions; a++)
		// value.set(s,a,initialValue);

		update = true;
	}

	public void run() {
		// Updates may be disabled for data log reasons
		if (update) {
			Float1dPortArray reward = (Float1dPortArray) getInPort("reward");
			Int1dPort takenAction = (Int1dPort) getInPort("takenAction");
			Float1dPortArray statesBefore = (Float1dPortArray) getInPort("statesBefore");
			Float1dPortArray statesAfter = (Float1dPortArray) getInPort("statesAfter");
			Float1dPortArray votesBefore = (Float1dPortArray) getInPort("votesBefore");
			Float1dPortArray votesAfter = (Float1dPortArray) getInPort("votesAfter");
			;
			FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
			// Gets the active state as computed at the beginning of the cycle
			int a = takenAction.get();

			// Maximize the action value after the movement
			float maxExpectedR = Float.NEGATIVE_INFINITY;
			for (int action = 0; action < votesAfter.getSize(); action++)
				if (maxExpectedR < votesAfter.get(action))
					maxExpectedR = votesAfter.get(action);

			// Do the update once for each state
			for (int stateBefore = 0; stateBefore < numStates; stateBefore++)
				// Dont bother if the activation is to small
				if (statesBefore.get(stateBefore) > EPS && a != -1)
					updateLastAction(stateBefore, a, maxExpectedR, reward,
							statesBefore, statesAfter, votesBefore, value);
		}
	}

	private void updateLastAction(int sBefore, int a, float maxERNextState,
			Float1dPortArray reward, Float1dPortArray statesBefore,
			Float1dPortArray statesAfter, Float1dPortArray votesBefore,
			FloatMatrixPort value) {

		float val = value.get(sBefore, a);
		float delta;
		// If eating cut the cycle - episodic ql
		// TODO: get eat distinction back
		// if (a == subject.getEatActionNumber())
		// // Just look at eating future prediction
		// delta = alpha
		// * (reward.get() + discountFactor
		// * actionVotesAfter.get(subject.getEatActionNumber()) - (val +
		// actionVotesBefore
		// .get(a)));
		// // For all other actions - normal ql
		// else
		// TODO: get the bh expectation back
		delta = alpha
				* (reward.get() + discountFactor * (maxERNextState) - (val + votesBefore
						.get(a)));
		// if (reward.get() > 0)
		// System.out.println(delta + " " + actionVotesBefore.get(a));
		// if (a == Utiles.eatAction)
		// System.out.println("Updating eat with delta " + delta);
		float newValue = statesBefore.get(sBefore) * (val + delta)
				+ (1 - statesBefore.get(sBefore)) * val;

		if (Float.isInfinite(newValue) || Float.isNaN(newValue))
			System.out.println("Numeric Error");
		value.set(sBefore, a, newValue);
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