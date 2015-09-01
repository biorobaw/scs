package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Bool1dPort;
import edu.usf.ratsim.micronsl.Int0dPort;
import edu.usf.ratsim.micronsl.Int1dPort;
import edu.usf.ratsim.micronsl.Module;

public class LastTriedToEatGoalDecider extends Module {

	public int[] goalFeeder;

	public LastTriedToEatGoalDecider(String name) {
		super(name);

		goalFeeder = new int[2];
		addOutPort("goalFeeder", new Int1dPort(this, goalFeeder));

		goalFeeder[0] = -1;
		goalFeeder[1] = -1;
	}

	public void run() {
		Bool1dPort subTriedToEat = (Bool1dPort) getInPort("subTriedToEat");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subTriedToEat.get()) {
			goalFeeder[1] = goalFeeder[0];
			goalFeeder[0] = closestFeeder.get();
		}

		if (Debug.printActiveGoal)
			System.out.println("LastTriedToEat GD: " + goalFeeder[0] + " "
					+ goalFeeder[1]);
	}

	public void newTrial() {
		goalFeeder[0] = -1;
		goalFeeder[1] = -1;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
