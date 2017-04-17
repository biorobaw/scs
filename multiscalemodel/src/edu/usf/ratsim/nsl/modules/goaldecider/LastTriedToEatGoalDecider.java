package edu.usf.ratsim.nsl.modules.goaldecider;

import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class LastTriedToEatGoalDecider extends Module {

	public Int0dPort goalFeeder;

	public LastTriedToEatGoalDecider(String name) {
		super(name);
		
		goalFeeder = new Int0dPort(this);

		addOutPort("goalFeeder", goalFeeder);

		// Initially in 0 - no last feeder eaten
		goalFeeder.set(0);
	}

	public void run() {
		Bool0dPort subTriedToEat = (Bool0dPort) getInPort("subTriedToEat");
		Int0dPort closestFeeder = (Int0dPort) getInPort("closestFeeder");
		
		if (subTriedToEat.get()) {
			int newGoal = closestFeeder.get();
//			System.out.println("Tried to eat from " + newGoal);
			goalFeeder.set(newGoal);
		}
	}

	public void newTrial() {
		// Initially in 0 - no last feeder eaten
		goalFeeder.set(-1);
	}
	
	public void newEpisode(){
		goalFeeder.set(-1);
	}

	@Override
	public boolean usesRandom() {
		return true;
	}
}
