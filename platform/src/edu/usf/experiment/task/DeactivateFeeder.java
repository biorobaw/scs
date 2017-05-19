package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class DeactivateFeeder extends Task {

	private int feeder;

	public DeactivateFeeder(ElementWrapper params) {
		super(params);

		feeder = params.getChildInt("feeder");
	}
	
	public void perform(Universe u, Subject s){
		u.setActiveFeeder(feeder, false);
	}


}
