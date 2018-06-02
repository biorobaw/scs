package edu.usf.experiment.task.feeder;

import java.util.List;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;

/**
 *Task to flash and activate a random feeder
 * 
 * @author ludo
 *
 */
public class PublishActiveFeeder extends Task {

	public PublishActiveFeeder(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		List<Feeder> activeFeeders = FeederUniverseUtilities.getActiveFeeders(fu.getFeeders());
		Globals.getInstance().put("activeFeeder", "" + activeFeeders.get(0).getId());
	}


}
