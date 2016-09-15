package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to release food from all active feeders, regardless of whether the robot
 * is close or not
 * 
 * @author ludo
 * 
 */
public class ReleaseFoodFromAllFeeders extends Task {

	public ReleaseFoodFromAllFeeders(ElementWrapper params) {
		super(params);
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
		for (Integer f : u.getEnabledFeeders())
			u.releaseFood(f);
	}

}
