package edu.usf.experiment.log;

import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class PlatformLogger extends Logger {

	public PlatformLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Universe univ) {
		PlatformUniverse fu = (PlatformUniverse) univ;
		
		synchronized (PlatformLogger.class) {
			PropertyHolder props = PropertyHolder.getInstance();
			String groupName = props.getProperty("group");
			String subName = props.getProperty("subject");

			PrintWriter writer = getWriter();
			for (Platform p : fu.getPlatforms())
				writer.println(groupName + '\t' + subName + '\t' 
						+ p.getPosition().x + '\t' + p.getPosition().y + '\t');
		}
	}

	@Override
	public void log(Episode episode) {
		log(episode.getUniverse());
	}

	@Override
	public void log(Trial trial) {
		log(trial.getUniverse());
	}

	public String getFileName() {
		return "feeders.csv";
	}

	@Override
	public void finalizeLog() {
	}

	@Override
	public String getHeader() {
		return "group\tsubject\tx\ty";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse());
	}

}
