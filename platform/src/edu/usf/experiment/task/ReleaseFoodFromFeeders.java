package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to activate all enabled feeders
 * @author ludo
 *
 */
public class ReleaseFoodFromFeeders extends Task {

	public ReleaseFoodFromFeeders(ElementWrapper params) {
		super(params);
	}
	public void perform(Universe u, Subject s){
		for (Integer f : u.getActiveFeeders())
			if(u.hasRobotFoundFeeder(f)){
				u.releaseFood(f);
			}
	}

}
