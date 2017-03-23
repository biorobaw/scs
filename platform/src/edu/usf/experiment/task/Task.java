package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Task {

	public Task(ElementWrapper params) {

	}

	public void perform(Experiment e){
		perform(e.getUniverse(), e.getSubject());
	}

	public void perform(Trial t){
		perform(t.getUniverse(), t.getSubject());
	}

	public void perform(Episode e){
		perform(e.getUniverse(), e.getSubject());
	}
	
	public abstract void perform(Universe u, Subject s);

}
