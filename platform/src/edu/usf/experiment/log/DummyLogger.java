package edu.usf.experiment.log;


import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyLogger extends DistributedLogger {

	public DummyLogger(ElementWrapper params, String logPath){
		super(params, logPath);
	}
	
	@Override
	public void log(Episode episode) {
		System.out.println("Logging");
	}

	@Override
	public void finalizeLog() {

	}

	@Override
	public String getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log(Trial trial) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(Experiment experiment) {
		// TODO Auto-generated method stub
		
	}

}
