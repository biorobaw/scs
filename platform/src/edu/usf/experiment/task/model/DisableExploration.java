package edu.usf.experiment.task.model;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.model.ExploratoryModel;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class DisableExploration extends Task{

	public DisableExploration(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		ExploratoryModel m = (ExploratoryModel) s.getModel();
		
		m.setExplorationVal(0);
	}

}
