package edu.usf.experiment.log;

import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public class CyclesLogger extends Logger {

	public CyclesLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void log(Episode episode) {
		log();
	}

	private void log() {
		
		synchronized (CyclesLogger.class) {
			PropertyHolder props = PropertyHolder.getInstance();
			String groupName = props.getProperty("group");
			String subName = props.getProperty("subject");
			String trial = props.getProperty("trial");
			int episode = Integer.parseInt(props.getProperty("episode"));
			int cycle = Integer.parseInt(props.getProperty("cycle"));
			
			PrintWriter writer = getWriter();
				writer.println(trial + '\t' + groupName + '\t' + subName + '\t' + episode + '\t' 
						+ cycle);
		}
	}

	@Override
	public void log(Trial trial) {

	}

	@Override
	public void log(Experiment experiment) {

	}

	@Override
	public void finalizeLog() {

	}
	
	

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\truntime";
	}

	@Override
	public String getFileName() {
		return "runtimes.csv";
	}

}
