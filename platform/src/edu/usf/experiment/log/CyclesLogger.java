package edu.usf.experiment.log;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public class CyclesLogger extends SingleFileLogger {

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
			
			append(trial + '\t' + groupName + '\t' + subName + '\t' + episode + '\t' 
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
	public String getFileName() {
		return "runtimes.csv";
	}

	@Override
	public String getName() {
		return "Runtimes";
	}

}
