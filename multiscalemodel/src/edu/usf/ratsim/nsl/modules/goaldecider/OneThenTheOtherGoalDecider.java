package edu.usf.ratsim.nsl.modules.goaldecider;

import java.util.List;

import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class OneThenTheOtherGoalDecider extends Module {

	public Int0dPort goalFeeder;
	private int currentGoal;
	private RobotOld robot;
	public static int lastAte;

	public OneThenTheOtherGoalDecider(String name, RobotOld r) {
		super(name);

		goalFeeder = new Int0dPort(this);
		addOutPort("goalFeeder", goalFeeder);

		lastAte = -1;
		currentGoal = -1;

		this.robot = r;
	}

	public void run() {
		Bool0dPort subAte = (Bool0dPort) getInPort("subAte");
		Bool0dPort subTriedToEat = (Bool0dPort) getInPort("subTriedToEat");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");

		// Flashing switches map to current flashing one
		if (robot.seesFlashingFeeder()) {
			currentGoal = robot.getFlashingFeeder().getId();
		}

		if (subAte.get()) {
			// If ate, switch to a random feeder from the other two
			lastAte = robot.getLastAteFeeder();
			if (Debug.printMaps)
				System.out.println("Setting last ate to " + lastAte);
			List<Integer> enabled = Universe.getEnabledFeeders();
			enabled.remove(new Integer(lastAte));
			currentGoal = enabled.get(RandomSingleton.getInstance().nextInt(
					enabled.size()));
			if (Debug.printMaps)
				System.out.println("Setting goal to " + currentGoal);
		} else if (subTriedToEat.get()
				&& robot.getLastTriedToEatFeeder() == currentGoal) {
			int prevGoal = currentGoal;
			// If tried to eat unsuccessfully switch to the other feeder
			List<Integer> enabled = Universe.getEnabledFeeders();
			enabled.remove(new Integer(lastAte));
			enabled.remove(new Integer(currentGoal));
			currentGoal = enabled.get(0);
			if (Debug.printMaps)
				System.out.println("Switching from goal " + prevGoal
						+ " to goal " + currentGoal);
		}

		goalFeeder.set(currentGoal);
	}

	public void newTrial() {
		lastAte = -1;
		currentGoal = -1;
		goalFeeder.set(currentGoal);
	}

	@Override
	public boolean usesRandom() {
		return true;
	}
}
