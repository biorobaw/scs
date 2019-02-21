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
	
	public abstract void initLog();
	
	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();
		finalizeLog();
	}
	 @Override
	public void endTrial() {
		// TODO Auto-generated method stub
		super.endTrial();
		finalizeLog();
	}
	 
	 @Override
	public void endExperiment() {
		// TODO Auto-generated method stub
		super.endExperiment();
		finalizeLog();
	}
	 
	 @Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		 super.newEpisode();
		initLog();
	}
	 
	 @Override
	public void newTrial() {
		// TODO Auto-generated method stub
		super.newTrial();
		initLog();
	}
	 
	 @Override
	public void newExperiment() {
		// TODO Auto-generated method stub
		super.newExperiment();
		initLog();
	}
	 
	 

	
	

}
