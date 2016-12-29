package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class DisableExploration extends Task{

	public DisableExploration(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		s.setExplorationVal(0);
	}

}
