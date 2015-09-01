//package edu.usf.ratsim.nsl.modules;
//
//import java.util.Random;
//
//import nslj.src.lang.NslModule;
//import edu.usf.experiment.utils.Utiles;
//import edu.usf.ratsim.experiment.ExperimentUniverse;
//import edu.usf.ratsim.robot.IRobot;
//
//public class Explorer extends NslModule {
//	private IRobot robot;
//	private Random r;
//
//	public Explorer(String nslName, NslModule nslParent, IRobot robot,
//			ExperimentUniverse universe) {
//		super(nslName, nslParent);
//		this.robot = robot;
//		r = RandomSingleton.getInstance();
//	}
//
//	public void simRun() {
//
//		boolean[] affordances;
//		int action;
//		do {
//			action = (int) Math.round(r.nextGaussian() * .5
//					+ GeomUtils.discretizeAction(0));
//			// Trim
//			action = action < 0 ? 0 : action;
//			action = action > GeomUtils.numActions ? GeomUtils.numActions - 1
//					: action;
//			// Rotate the robot to the desired action
//			robot.rotate(GeomUtils.getActionAngle(action));
//			// Re-calculate affordances
//			affordances = robot.getAffordances();
//		} while (!affordances[GeomUtils.discretizeAction(0)]);
//
//		robot.forward();
//	}
// }
