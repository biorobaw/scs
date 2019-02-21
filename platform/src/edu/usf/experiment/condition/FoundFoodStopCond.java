package edu.usf.experiment.condition;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundFoodStopCond implements Condition {

	public FoundFoodStopCond(ElementWrapper params) {
	}

	@Override
	public boolean holds() {
		return ((FeederUniverse)Universe.getUniverse()).hasRobotEaten();
	}
	

}
