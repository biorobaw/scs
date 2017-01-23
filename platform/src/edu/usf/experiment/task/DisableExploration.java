package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.utils.ElementWrapper;

public class DisableExploration extends Task{

	public DisableExploration(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Experiment experiment) {
		perform((SubjectOld)experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform((SubjectOld)trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform((SubjectOld)episode.getSubject());
	}

	private void perform(SubjectOld subject) {
		subject.setExplorationVal(0);
	}

}
