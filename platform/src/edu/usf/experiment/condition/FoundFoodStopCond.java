package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundFoodStopCond implements Condition {

	public FoundFoodStopCond(ElementWrapper params) {
	}

	@Override
	public boolean holds(Episode episode) {
		return episode.getSubject().hasEaten();
	}
	

}
