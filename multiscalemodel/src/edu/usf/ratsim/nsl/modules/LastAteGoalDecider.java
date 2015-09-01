package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Bool1dPort;
import edu.usf.ratsim.micronsl.Int0dPort;
import edu.usf.ratsim.micronsl.Int1dPort;
import edu.usf.ratsim.micronsl.Module;

public class LastAteGoalDecider extends Module {

	public int[] goalFeeder;
	public static int currentGoal;

	public LastAteGoalDecider(String name) {
		super(name);

		goalFeeder = new int[1];
		addOutPort("goalFeeder", new Int1dPort(this, goalFeeder));

		currentGoal = -1;
	}

	public void run() {
		Bool1dPort subAte = (Bool1dPort) getInPort("subAte");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subAte.get()) {
			currentGoal = closestFeeder.get();
		}

		goalFeeder[0] = currentGoal;
		if (Debug.printActiveGoal)
			System.out.println("Last Ate GD: " + currentGoal);
	}

	public void newTrial() {
		currentGoal = -1;
		goalFeeder[0] = currentGoal;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
