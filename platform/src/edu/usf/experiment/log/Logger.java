package edu.usf.experiment.log;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Logger {

	public Logger(ElementWrapper params, String logPath){
	}

	public abstract void log(Universe u,Subject s);


	public abstract void finalizeLog();

	public abstract String getFileName();
	

}
