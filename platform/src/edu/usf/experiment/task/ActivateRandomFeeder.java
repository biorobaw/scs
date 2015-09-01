package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
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

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}
	
	private void perform(Universe u) {
		List<Integer> enabledFeeders = u.getEnabledFeeders();

		Random r = RandomSingleton.getInstance();
		int feeder = enabledFeeders.get(r.nextInt(enabledFeeders.size()));
		u.setActiveFeeder(feeder, true);
	}


}
