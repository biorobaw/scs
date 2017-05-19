package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to activate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class ActivateRandomFeeder extends Task {

	public ActivateRandomFeeder(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
	}

	
	public void perform(Universe u, Subject s) {
		List<Integer> enabledFeeders = u.getEnabledFeeders();

		Random r = RandomSingleton.getInstance();
		int feeder = enabledFeeders.get(r.nextInt(enabledFeeders.size()));
		u.setActiveFeeder(feeder, true);
	}


}
