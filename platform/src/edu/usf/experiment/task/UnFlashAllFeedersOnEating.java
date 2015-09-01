package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate all enabled feeders
 * @author ludo
 *
 */
public class UnFlashAllFeedersOnEating extends Task {

	public UnFlashAllFeedersOnEating(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse(), experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), episode.getSubject());
	}
	
	private void perform(Universe u, Subject subject){
		if (subject.hasEaten())
			for (Integer f : u.getFeederNums())
				u.setFlashingFeeder(f, false);
	}

}
