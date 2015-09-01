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

		// Deactivate all to deactivate previous feeder
//		for(Integer f : u.getActiveFeeders())
//			u.setActiveFeeder(f, false);
		
		Random r = RandomSingleton.getInstance();
		int feeder = enabledFeeders.get(r.nextInt(enabledFeeders.size()));
		u.setActiveFeeder(feeder, true);
		u.setFlashingFeeder(feeder, true);
	}


}
