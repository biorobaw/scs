package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
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
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		for (Feeder f : FeederUniverseUtilities.getEnabledFeeders(fu.getFeeders())){
			fu.setActiveFeeder(f.getId(), false);
			fu.clearFoodFromFeeder(f.getId());
		}
	}

}
