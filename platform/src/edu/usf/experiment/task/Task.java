package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Task {

	public Task(ElementWrapper params) {

	}

	public abstract void perform(Experiment experiment);

	public abstract void perform(Trial trial);

	public abstract void perform(Episode episode);

}
