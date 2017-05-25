package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to activate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class ActivateFeeder extends Task {

	private int feeder;

	public ActivateFeeder(ElementWrapper params) {
		super(params);

		feeder = params.getChildInt("feeder");
	}
	
	public void perform(Universe u, Subject s) {
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		fu.setActiveFeeder(feeder, true);
	}


}
