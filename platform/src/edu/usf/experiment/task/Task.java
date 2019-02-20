package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Task {

	public Task(ElementWrapper params) {

	}

	
	public abstract void perform(Universe u, Subject s);
	
	
	public void newEpisode() {
		
	}
	
	public void endEpisode() {
		
	}
	
	public void newTrial() {
		
	}
	
	public void endTrial() {
		
	}
	
	public void newExperiment() {
		
	}
	
	public void endExperiment() {
		
	}
	

}
