package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;


public class ClearWallsTask extends Task {

	public ClearWallsTask(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		u.clearWalls();
	}

}
