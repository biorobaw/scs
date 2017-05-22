package edu.usf.ratsim.nsl.modules.goaldecider;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class LastAteGoalDecider extends Module {

	private Int0dPort goalPort;
	public static int currentGoal;

	public LastAteGoalDecider(String name) {
		super(name);

		goalPort = new Int0dPort(this);
		addOutPort("goalFeeder", goalPort);
		
		currentGoal = -1;
	}

	public void run() {
		Bool0dPort subAte = (Bool0dPort) getInPort("subAte");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subAte.get()) {
			currentGoal = closestFeeder.get();				
		}

		goalPort.set(currentGoal);
		if (Debug.printActiveGoal)
			System.out.println("Last Ate GD: " + currentGoal);
	}

	public void newTrial() {
		currentGoal = -1;
		goalPort.set(currentGoal);
	}

	@Override
	public boolean usesRandom() {
		return true;
	}
}
