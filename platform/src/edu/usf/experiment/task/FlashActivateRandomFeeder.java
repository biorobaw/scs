package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.universe.Universe;
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
		
		List<Integer> enabledFeeders = fu.getEnabledFeeders();

		Random r = RandomSingleton.getInstance();
		int feeder = enabledFeeders.get(r.nextInt(enabledFeeders.size()));
		fu.setActiveFeeder(feeder, true);
		fu.setFlashingFeeder(feeder, true);
	}


}
