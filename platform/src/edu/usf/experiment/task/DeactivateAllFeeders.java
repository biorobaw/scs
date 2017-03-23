package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate all enabled feeders
 * @author ludo
 *
 */
public class DeactivateAllFeeders extends Task {

	public DeactivateAllFeeders(ElementWrapper params) {
		super(params);
	}
	
	public void perform(Universe u, Subject s){
		for (Integer f : u.getEnabledFeeders()){
			u.setActiveFeeder(f, false);
			u.clearFoodFromFeeder(f);
		}
	}

}
