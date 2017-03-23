package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate all enabled feeders
 * @author ludo
 *
 */
public class UnFlashAllFeeders extends Task {

	public UnFlashAllFeeders(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		for (Integer f : u.getFeederNums())
			u.setFlashingFeeder(f, false);
	}

}
