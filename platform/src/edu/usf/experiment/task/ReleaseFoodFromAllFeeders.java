package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to release food from all active feeders, regardless of whether the robot
 * is close or not
 * 
 * @author ludo
 * 
 */
public class ReleaseFoodFromAllFeeders extends Task {

	public ReleaseFoodFromAllFeeders(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		for (Integer f : fu.getEnabledFeeders())
			fu.releaseFood(f);
	}

}
