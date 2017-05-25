package edu.usf.experiment.task.feeder;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 *Task to flash and activate a random feeder
 * 
 * @author ludo
 *
 */
public class FlashActivateRandomFeeder extends Task {

	public FlashActivateRandomFeeder(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		List<Feeder> enabledFeeders = FeederUniverseUtilities.getEnabledFeeders(fu.getFeeders());

		Random r = RandomSingleton.getInstance();
		Feeder feeder = enabledFeeders.get(r.nextInt(enabledFeeders.size()));
		fu.setActiveFeeder(feeder.getId(), true);
		fu.setFlashingFeeder(feeder.getId(), true);
	}


}
