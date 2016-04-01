package edu.usf.ratsim.nsl.modules.actionselection;
//package edu.usf.ratsim.nsl.modules.qlearning.actionselection;
//
//import java.util.Random;
//
//import nslj.src.lang.NslDinFloat1;
//import nslj.src.lang.NslDoutInt0;
//import nslj.src.lang.NslModule;
//import edu.usf.experiment.subject.Subject;
//import edu.usf.experiment.universe.Universe;
//import edu.usf.ratsim.robot.IRobot;
//
//public class ProportionalExplorer extends NslModule {
//
//	public float aprioriValueVariance;
//
//	public NslDinFloat1[] votes;
//	public NslDoutInt0 takenAction;
//
//	private IRobot robot;
//
//	private Random r;
//
//	private boolean explore;
//
//	private Universe universe;
//
//	private Subject subject;
//
//	// private float maxPossibleReward;
//
//	public ProportionalExplorer(String nslName, NslModule nslParent, Subject subject,
//			int numLayers) {
//		super(nslName, nslParent);
//
//		this.subject = subject;
//
//		takenAction = new NslDoutInt0(this, "takenAction");
//
//		votes = new NslDinFloat1[numLayers];
//		for (int i = 0; i < numLayers; i++)
//			votes[i] = new NslDinFloat1(this, "votes" + i);
//
//		r = RandomSingleton.getInstance();
//	}
//
//	public void simRun() {
//		// TODO: get proportional explorer back
////		float[] overallValues = new float[subject.getNumActions()];
////		for (int i = 0; i < overallValues.length; i++)
////			overallValues[i] = 0;
////		// Add each contribution
////		for (NslDinFloat1 layerVal : votes)
////			for (int angle = 0; angle < layerVal.getSize(); angle++)
////				overallValues[angle] += layerVal.get(angle);
////		// find total value with laplacian
////		float maxVal = Float.MIN_VALUE;
////		for (int angle = 0; angle < overallValues.length; angle++)
////			if (maxVal < overallValues[angle])
////				maxVal = overallValues[angle];
////
////		// if (maxVal > 1)
////		// System.out.println(maxVal);
////
////		// explore = r.nextFloat() > (maxVal / maxPossibleReward);
////		// if (explore)
////		// System.out.println("Exploring");
////		// explore = maxVal == 0;
////		// System.out.println(maxVal);
////		// Make a list of actions and values
////		List<ActionValue> actions = new LinkedList<ActionValue>();
////		for (int angle = 0; angle < overallValues.length; angle++) {
////			actions.add(new ActionValue(angle, overallValues[angle]));
////		}
////		// Collections.sort(actions);
////
////		// Recompute max val
////		maxVal = Float.MIN_VALUE;
////		for (int a = 0; a < overallValues.length; a++)
////			if (maxVal < overallValues[a])
////				maxVal = overallValues[a];
////
////		int action;
////		// Roulette algorithm
////		// Get total value
////		// Find min val
////		float minVal = 0;
////		for (ActionValue aValue : actions)
////			if (aValue.getValue() < minVal)
////				minVal = aValue.getValue();
////		// Get max value
////		float totalVal = 0;
////		for (ActionValue aValue : actions)
////			// Substract min val to raise everything above 0
////			totalVal += aValue.getValue() - minVal;
////		// Calc a new random in [0, totalVal]
////		float nextRVal = r.nextFloat() * totalVal;
////		// Find the next action
////		action = -1;
////		if (actions.isEmpty())
////			System.out.println("no actions");
////		do {
////			action++;
////			nextRVal -= (actions.get(action).getValue() - minVal);
////		} while (nextRVal >= 0 && action < actions.size() - 1);
////
////		// Try the selected action
////		if (actions.get(action).getAction() == subject.getEatActionNumber()){
////			System.out.println("Eating");
////			robot.eat();
////		} else {
////			robot.rotate(subject.getActionAngle(actions.get(action).getAction()));
////			boolean[] aff = robot.getAffordances();
////			// Random if there was no affordable positive value action
////			// lastActionRandom = actions.get(action).getValue() <=
////			// EXPLORATORY_VARIANCE;
////	//		actions.remove(action);
////			// } while (!aff[Utiles.discretizeAction(0)]);
////	
////			
////			if (aff[subject.getActionForward()])
////				robot.forward();
////		}
////		// Publish the taken action
////		takenAction.set(actions.get(action).getAction());
////
////		// Now it is safe to forward
////		// if (!aff[Utiles.discretizeAction(0)]) {
////		// if (Math.random() > .5)
////		// robot.rotate((float) (Math.PI / 2));
////		// else {
////		// robot.rotate((float) (-Math.PI / 2));
////		// }
////		// aff = robot.getAffordances();
////		// }
//
//	}
//
//	public boolean wasLastActionRandom() {
//		return explore;
//	}
// }
