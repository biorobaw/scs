package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;

public class DisableExploration extends Task{

	public DisableExploration(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getSubject());
	}

	private void perform(Subject subject) {
		subject.setExplorationVal(0);
	}

}
