//package edu.usf.ratsim.nsl.modules.qlearning.update;
//
//import java.awt.geom.Point2D;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//
//import nslj.src.lang.NslDinFloat0;
//import nslj.src.lang.NslDinFloat1;
//import nslj.src.lang.NslDinInt0;
//import nslj.src.lang.NslDoutFloat2;
//import nslj.src.lang.NslModule;
//import edu.usf.experiment.subject.Subject;
//import edu.usf.experiment.universe.Universe;
//import edu.usf.ratsim.nsl.modules.qlearning.QLSupport;
//import edu.usf.ratsim.support.Configuration;
//import edu.usf.ratsim.support.GeomUtils;
//
//public class SingleStateQL extends NslModule implements QLAlgorithm {
//
//	private static final float INTERVAL = 0.05f;
//	// Margin for ignoring inside maze
//	private static final float MARGIN = 0.1f;
//	private static final float ANGLE_INTERVAL = 0.314f;
//
//	private static final String DUMP_FILENAME = "policy.txt";
//
//	private static PrintWriter writer;
//	private NslDinFloat0 reward;
//	private NslDinInt0 takenAction;
//	private NslDoutFloat2 value;
//	private NslDinFloat1 statesBefore;
//
//	private float alpha;
//	private float discountFactor;
//	private NslDinFloat1 statesAfter;
//	private int numStates;
//	private boolean update;
//
//	public SingleStateQL(String nslMain, NslModule nslParent, int numStates,
//			int numActions, float discountFactor, float alpha,
//			float initialValue) {
//		super(nslMain, nslParent);
//
//		this.discountFactor = discountFactor;
//		this.alpha = alpha;
//		this.numStates = numStates;
//
//		takenAction = new NslDinInt0(this, "takenAction");
//		reward = new NslDinFloat0(this, "reward");
//		statesBefore = new NslDinFloat1(this, "statesBefore", numStates);
//		statesAfter = new NslDinFloat1(this, "statesAfter", numStates);
//
//		value = new NslDoutFloat2(this, "value", numStates, numActions);
//		value.set(initialValue);
//		// for (int s = 0; s < numStates; s++)
//		// for (int a = 0; a < numActions; a++)
//		// value.set(s,a,initialValue);
//
//		update = true;
//	}
//
//	public void simRun() {
//		if (update) {
//			// Gets the active state as computed at the beginning of the cycle
//			int sBefore = getActiveState(statesBefore);
//			int sAfter = getActiveState(statesAfter);
//			int a = takenAction.get();
//			updateLastAction(sBefore, sAfter, a);
//		}
//	}
//
//	private void updateLastAction(int sBefore, int sAfter, int a) {
//		float maxERNextState;
//		if (a != -1)
//			maxERNextState = getMaxExpectedReward(value, sAfter);
//		else
//			maxERNextState = 0;
//
//		float actionValue = value.get(sBefore, a);
//		float newValue = actionValue
//				+ alpha
//				* (reward.get() + discountFactor * maxERNextState - actionValue);
//
//		value.set(sBefore, a, newValue);
//		// System.out.println(sBefore);
//		// if (actionValue != value.get(sBefore, a))
//		// System.out.println(actionValue + " " + value.get(sBefore, a));
//	}
//
//	private float getMaxExpectedReward(NslDoutFloat2 value, int s) {
//		float maxER = value.get(s, 0);
//		for (int a = 1; a < value.getSize2(); a++) {
//			if (value.get(s, a) > maxER)
//				maxER = value.get(s, a);
//		}
//
//		return maxER;
//	}
//
//	private int getActiveState(NslDinFloat1 states) {
//		// Winner take all within the layer
//		float maxVal = 0;
//		int activeState = -1;
//		for (int i = 0; i < states.getSize(); i++)
//			if (states.get(i) > maxVal) {
//				activeState = i;
//				maxVal = states.get(i);
//			}
//
//		return activeState;
//	}
//
//	/**
//	 * Dumps the qlearning policy with a certain intention to a file. The
//	 * alignment between pcl cells and ql states is assumed for efficiency
//	 * purposes.
//	 * 
//	 * @param rep
//	 * @param subName
//	 * @param trial
//	 * @param rep
//	 * 
//	 * @param writer
//	 * @param pcl
//	 */
//	public void dumpPolicy(String trial, String groupName, String subName,
//			String rep, int numIntentions, ExperimentUniverse univ,
//			ExpSubject rat) {
//		synchronized (QLSupport.class) {
//			// Deactivate updates
//			((RLRatModel) rat.getModel()).setPassiveMode(true);
//			PrintWriter writer = SingleStateQL.getWriter();
//
//			for (int intention = 0; intention < numIntentions; intention++) {
//				for (float xInc = MARGIN; xInc
//						- (univ.getBoundingRectangle().getWidth() - MARGIN / 2) < 1e-8; xInc += INTERVAL) {
//					for (float yInc = MARGIN; yInc
//							- (univ.getBoundingRectangle().getHeight() - MARGIN / 2) < 1e-8; yInc += INTERVAL) {
//						float x = (float) (univ.getBoundingRectangle()
//								.getMinX() + xInc);
//						float y = (float) (univ.getBoundingRectangle()
//								.getMinY() + yInc);
//
//						// List<Float> preferredAngles = new
//						// LinkedList<Float>();
//						float maxVal = Float.NEGATIVE_INFINITY;
//						float bestAngle = 0;
//						for (float angle = 0; angle <= 2 * Math.PI; angle += ANGLE_INTERVAL) {
//							univ.setRobotPosition(new Point2D.Float(x, y),
//									angle);
//							rat.stepCycle();
//							// // float forwardVal =
//							// ((MultiScaleMultiIntentionCooperativeModel) rat
//							// //
//							// .getModel()).getQLVotes().getVotes().get(Utiles.discretizeAction(0));
//							// if( forwardVal > maxVal){
//							// maxVal = forwardVal;
//							// bestAngle = angle;
//							// }
//							for (int action = 0; action < GeomUtils.numActions; action++) {
//								float angleVal = ((MultiScaleMultiIntentionCooperativeModel) rat
//										.getModel()).getQLVotes().getVotes()
//										.get(action);
//								if (angleVal > maxVal) {
//									maxVal = angleVal;
//									bestAngle = angle;
//								}
//							}
//
//							// If goes forward, it is the preferred angle
//						}
//
//						String preferredAngleString = new Float(bestAngle)
//								.toString();
//
//						writer.println(trial + '\t' + groupName + '\t'
//								+ subName + '\t' + rep + '\t' + x + "\t" + y
//								+ "\t" + intention + "\t"
//								+ preferredAngleString + "\t" + maxVal);
//
//					}
//				}
//			}
//			// Re enable updates
//			((RLRatModel) rat.getModel()).setPassiveMode(false);
//			univ.clearRobotAte();
//		}
//	}
//
//	private static PrintWriter getWriter() {
//		if (writer == null) {
//			try {
//				writer = new PrintWriter(new OutputStreamWriter(
//						new FileOutputStream(new File(Configuration
//								.getString("Log.DIRECTORY") + DUMP_FILENAME))),
//						true);
//				writer.println("trial\tgroup\tsubject\trepetition\tx\ty\tintention\theading\tval");
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		return writer;
//	}
//
//	public int getMaxAngle(int s) {
//		float[] vals = new float[GeomUtils.numAngles];
//		int maxAngle = -1;
//		float maxVal = 0;
//		for (int angle = 0; angle < GeomUtils.numAngles; angle++) {
//			vals[angle] = value.get(s, angle);
//			if (vals[angle] > maxVal) {
//				maxVal = vals[angle];
//				maxAngle = angle;
//			}
//		}
//		return maxAngle;
//	}
//
//	@Override
//	public void setUpdatesEnabled(boolean b) {
//		update = false;
//	}
//
//	@Override
//	public void savePolicy() {
//		
//	}
//
//	@Override
//	public void dumpPolicy(String trial, String groupName, String subName,
//			String rep, int numIntentions, Universe universe, Subject subject) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	
// }