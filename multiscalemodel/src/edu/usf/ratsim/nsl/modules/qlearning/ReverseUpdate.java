package edu.usf.ratsim.nsl.modules.qlearning;
//package edu.usf.ratsim.nsl.modules.qlearning.update;
//
//import nslj.src.lang.NslModule;
//import edu.usf.ratsim.nsl.modules.qlearning.QLSupport;
//import edu.usf.ratsim.nsl.modules.qlearning.StateActionReward;
//import edu.usf.ratsim.robot.IRobot;
//import edu.usf.ratsim.support.Configuration;
//
//public class ReverseUpdate extends NslModule {
//
//	private ExperimentUniverse universe;
//
//	private QLSupport qlData;
//
//	private float alpha;
//
//	private float discountFactor;
//
//	public ReverseUpdate(String nslMain, NslModule nslParent, int stateSize,
//			QLSupport qlData, IRobot robot, ExperimentUniverse universe) {
//		super(nslMain, nslParent);
//
//		this.universe = universe;
//		this.qlData = qlData;
//
//		discountFactor = Configuration.getFloat("QLearning.discountFactor");
//		alpha = Configuration.getFloat("QLearning.learningRate");
//	}
//
//	public void simRun() {
//		updateLastAction();
//	}
//
//	private void updateLastAction() {
//		if (qlData.numVisitedSA() >= 2) {
//			// Pick the last state action, which is at position 0
//			StateActionReward last = qlData.getVisitedSA(0);
//			StateActionReward beforeLast = qlData.getVisitedSA(1);
//
//			// Actions and states are not stored in phase - actions are one
//			// stateaction after
//			StateActionReward performed = new StateActionReward(
//					beforeLast.getState(), last.getAction());
//
//			int action = qlData.getMaxAngle(last.getState());
//			float maxNextState = qlData.getValue(new StateActionReward(last
//					.getState(), action));
//			float reward = last.getReward();
//
//			float value = qlData.getValue(performed);
//			float newValue = value + alpha
//					* (reward + discountFactor * maxNextState - value);
//
//			qlData.setValue(performed, newValue);
//
//			qlData.popLastRecord();
//		}
//	}
//
//	public void updateQValueFood() {
////		float reward;
////		// If has found food, start with food reward
////		if (universe.hasRobotFoundFood())
////			reward = FOOD_REWARD;
////		else
////			// reward = NON_FOOD_REWARD;
////			reward = 0; // TODO: if it stays like that, fix efficiency
////
////		// The current heading referst to the last taken action
////		int currHeading = Utiles.discretizeAngle(universe
////				.getRobotOrientationAngle());
////		// The state is never going to be used
////		StateActionReward nextSA = new StateActionReward(0, currHeading);
////		// Keep the max of the next state to pass on the next iter
////		float maxNextState = 0;
////		for (int i = 0; i < qlData.numVisitedSA(); i++) {
////			StateActionReward previusSA = qlData.getVisitedSA(i);
////
////			StateActionReward prevStateNextAction = new StateActionReward(
////					previusSA.getState(), nextSA.getAction());
////
////			// The value to update corresponds to the state recorded in
////			// previous iteration and the action recorded in the following
////			float value = qlData.getValue(prevStateNextAction);
////
////			// Compute new value
////			float newValue = value + alpha
////					* (reward + discountFactor * maxNextState - value);
////
////			// System.out.println(newValue);
////			qlData.setValue(prevStateNextAction, newValue);
////
////			maxNextState = 0;
////			// Maximize posible outcome of this state and save it for next
////			// iter
////			for (int a = 0; a < Utiles.discreteAngles.length; a++) {
////				float aVal = qlData.getValue(new StateActionReward(
////						prevStateNextAction.getState(), a));
////				if (aVal > maxNextState) {
////					maxNextState = aVal;
////				}
////			}
////
////			// This state is the state the next one arrived to (reverse
////			// order)
////			nextSA = previusSA;
////
////			// Following state-actions have non-food rewards (only one has found
////			// food state)
////			// reward = NON_FOOD_REWARD;
////			reward = 0;
////		}
////
////		qlData.clearRecord();
//	}
//
// }