package edu.usf.experiment.log;

import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class FeederLogger extends DistributedLogger {

	public FeederLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Universe univ) {
		if (!(univ instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) univ;
		
		synchronized (FeederLogger.class) {
			Globals g = Globals.getInstance();
			String groupName = g.get("group").toString();
			String subName = g.get("subName").toString();

			PrintWriter writer = getWriter();
			for (Feeder f : fu.getFeeders())
				writer.println(groupName + '\t' + subName + '\t' + f.getId() + '\t' 
						+ f.getPosition().x + '\t' + f.getPosition().y + '\t'
						+ f.isEnabled());
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
		return "group\tsubject\tid\tx\ty\tenabled";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse());
	}

}
