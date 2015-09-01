package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to activate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class ActivateFeeder extends Task {

	private int feeder;

	public ActivateFeeder(ElementWrapper params) {
		super(params);

		feeder = params.getChildInt("feeder");
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
		u.setActiveFeeder(feeder, true);
	}


}
