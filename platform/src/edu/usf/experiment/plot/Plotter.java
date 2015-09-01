package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;

public abstract class Plotter {
	
	private String logPath;

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public Plotter(ElementWrapper params, String logPath){
		this.logPath = logPath;
	}

	public abstract void plot();

}
