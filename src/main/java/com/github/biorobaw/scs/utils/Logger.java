package com.github.biorobaw.scs.utils;

import com.github.biorobaw.scs.experiment.task.cycle.CycleTask;

public abstract class Logger extends CycleTask {

	public Logger(XML params){
		super(params);
	}

	public abstract void finalizeLog();	
	
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
	
	

}
