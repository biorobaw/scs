package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundPlatform implements Condition {
	

	public FoundPlatform(ElementWrapper params){
	}

	@Override
	public boolean holds(Episode e) {
		return e.getUniverse().hasRobotFoundPlatform();
	}

}
