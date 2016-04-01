package edu.usf.ratsim.nsl.modules.goaldecider;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;

public class ActiveFeederGoalDecider extends Module {

	public int[] goalFeeder;
	public static int currentGoal;

	public ActiveFeederGoalDecider(String name) {
		super(name);

		goalFeeder = new int[1];
		addOutPort("goalFeeder", new Int1dPortArray(this, goalFeeder));
		
		currentGoal = -1;
	}

	public void run() {
		currentGoal = Universe.getActiveFeeders().get(0);

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
		return true;
	}
}
