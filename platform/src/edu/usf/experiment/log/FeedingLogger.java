package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.LinkedList;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class FeedingLogger extends Logger {

	private LinkedList<FeedingLog> feederLogs;

	public FeedingLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		feederLogs = new LinkedList<FeedingLog>();
	}

	@Override
	public void log(Episode episode) {
		log(episode.getSubject(), episode.getUniverse());

	}

	private void log(Subject subject, Universe universe) {
		if (subject.hasTriedToEat()) {
			int feeder = universe.getFoundFeeder();
			FeedingLog fl = new FeedingLog(feeder, subject.hasEaten(),
					universe.isFeederFlashing(feeder));
			feederLogs.add(fl);
		}

	}

	@Override
	public void log(Trial trial) {
		log(trial.getSubject(), trial.getUniverse());
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getSubject(), experiment.getUniverse());
	}

	@Override
	public void finalizeLog() {
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episode = props.getProperty("episode");

		synchronized (PositionLogger.class) {
			PrintWriter writer = getWriter();
			for (FeedingLog fl : feederLogs)
				writer.println(trialName + '\t' + groupName + '\t' + subName
						+ '\t' + episode + '\t' + fl.feederId + "\t" + fl.ate
						+ "\t" + fl.wasFlashing);
			feederLogs.clear();
		}
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tfeeder\tate\tflashing";
	}

	@Override
	public String getFileName() {
		return "atefeeders.csv";
	}

}
