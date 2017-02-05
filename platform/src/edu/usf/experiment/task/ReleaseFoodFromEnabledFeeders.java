package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to activate all enabled feeders
 * @author ludo
 *
 */
public class ReleaseFoodFromEnabledFeeders extends Task {

	public ReleaseFoodFromEnabledFeeders(ElementWrapper params) {
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
	
	private void perform(Universe u){
		for (Integer f : Universe.getActiveFeeders())
			if(u.isFeederEnabled(f)){
				u.releaseFood(f);
			}
	}

}
