//package edu.usf.ratsim.nsl.modules;
//
//import java.util.List;
//import java.util.Random;
//
//import nslj.src.lang.NslDoutInt0;
//import nslj.src.lang.NslModule;
//import edu.usf.ratsim.experiment.ExperimentUniverse;
//import edu.usf.ratsim.experiment.plot.multifeeders.MultiFeedersTrialPlotter;
//import edu.usf.ratsim.experiment.subject.MultiScaleArtificialPCSubject;
//import edu.usf.ratsim.support.Debug;
//
///***
// * Sets the goal feeder to be one random from the active ones Selection occurs
// * only once and should be reseted after each episode
// * 
// * @author ludo
// *
// */
//public class ActiveGoalDecider extends NslModule {
//
//	public NslDoutInt0 goalFeeder;
//	public static int currentGoal;
//	private Random r;
//	private MultiScaleArtificialPCSubject subject;
//
//	public ActiveGoalDecider(String nslName, NslModule nslParent,
//			MultiScaleArtificialPCSubject subject) {
//		super(nslName, nslParent);
//		
//		this.subject = subject;
//
//		goalFeeder = new NslDoutInt0(this, "goalFeeder");
//
//		r = RandomSingleton.getInstance();
//		currentGoal = -1;
//	}
//
//	public void simRun() {
//		if (currentGoal == -1) {
//			List<Integer> active = subject.get
//			currentGoal = active.get(r.nextInt(active.size()));
//		}
//
//		if (currentGoal != -1)
//			universe.setWantedFeeder(currentGoal, true);
//
//		goalFeeder.set(currentGoal);
//		if (Debug.printActiveGoal)
//			System.out.println("Active GD: " + goalFeeder.get() + " "
//					+ currentGoal);
//	}
//
//	public void newTrial() {
//		currentGoal = -1;
//		goalFeeder.set(currentGoal);
//		universe.clearWantedFeeders();
//	}
// }
