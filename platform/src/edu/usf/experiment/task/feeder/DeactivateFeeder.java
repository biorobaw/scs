package edu.usf.experiment.task.feeder;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
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
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		fu.setActiveFeeder(feeder, false);
	}


}
