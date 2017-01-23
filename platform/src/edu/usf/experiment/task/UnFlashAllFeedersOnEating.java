package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.SubjectOld;
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
		perform(experiment.getUniverse(),(SubjectOld) experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), (SubjectOld)trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), (SubjectOld)episode.getSubject());
	}
	
	private void perform(Universe u, SubjectOld subject){
		if (subject.hasEaten())
			for (Integer f : u.getFeederNums())
				u.setFlashingFeeder(f, false);
	}

}
