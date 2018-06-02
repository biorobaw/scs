package edu.usf.ratsim.nsl.modules.goaldecider;

import edu.usf.experiment.Globals;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;

/**
 * This class selects the next intention as the currently active feeder, used in modes in which the agent is told which is the currently active feeder,
 * e.g. the taxi problem
 * @author martin
 *
 */
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
		// Get the active feeder from the published property
		currentGoal = Integer.parseInt(Globals.getInstance().get("activeFeeder").toString());

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
