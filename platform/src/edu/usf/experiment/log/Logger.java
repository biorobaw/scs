package edu.usf.experiment.log;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Logger {

	public Logger(ElementWrapper params, String logPath){
	}

	public abstract void log(Episode episode);
	
	public abstract void log(Trial trial);
	public abstract void log(Experiment experiment);

	public abstract void finalizeLog();

	public abstract String getFileName();

}
