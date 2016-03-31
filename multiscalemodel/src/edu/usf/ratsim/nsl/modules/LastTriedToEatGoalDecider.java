package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class LastTriedToEatGoalDecider extends Module {

	public int[] goalFeeder;

	public LastTriedToEatGoalDecider(String name) {
		super(name);

		goalFeeder = new int[2];
		addOutPort("goalFeeder", new Int1dPortArray(this, goalFeeder));

		goalFeeder[0] = -1;
		goalFeeder[1] = -1;
	}

	public void run() {
		Bool0dPort subTriedToEat = (Bool0dPort) getInPort("subTriedToEat");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subTriedToEat.get()) {
//			goalFeeder[1] = goalFeeder[0];
//			goalFeeder[0] = closestFeeder.get();
			int newGoal;
			do{
				newGoal = RandomSingleton.getInstance().nextInt(3);
			} while (newGoal == goalFeeder[0]);
			goalFeeder[0] = newGoal;
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
		return true;
	}
}
