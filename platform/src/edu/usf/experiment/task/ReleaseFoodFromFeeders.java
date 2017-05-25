package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
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
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;
		
		for (Feeder f : FeederUniverseUtilities.getActiveFeeders(fu.getFeeders()))
			if(FeederUniverseUtilities.isRobotCloseToFeeder(f, gcu.getRobotPosition(), fu.getCloseThrs())){
				fu.releaseFood(f.getId());
			}
	}

}
