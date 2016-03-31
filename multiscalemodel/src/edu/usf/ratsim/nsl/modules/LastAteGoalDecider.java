package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Bool1dPort;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class LastAteGoalDecider extends Module {

	public int[] goalFeeder;
	public static int currentGoal;

	public LastAteGoalDecider(String name) {
		super(name);

		goalFeeder = new int[1];
		addOutPort("goalFeeder", new Int1dPortArray(this, goalFeeder));
		
		currentGoal = -1;
	}

	public void run() {
		Bool1dPort subAte = (Bool1dPort) getInPort("subAte");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subAte.get()) {
			//currentGoal = closestFeeder.get();
			int newGoal;
			do{
				newGoal = RandomSingleton.getInstance().nextInt(3);
			} while (newGoal == currentGoal);
			currentGoal = newGoal;
				
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
		return true;
	}
}
