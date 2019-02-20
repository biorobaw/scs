package edu.usf.experiment.log;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Logger extends Task {

	public Logger(ElementWrapper params){
		super(params);
	}

	public abstract void perform(Universe u,Subject s);


	public abstract void finalizeLog();
	
	

	
	

}
