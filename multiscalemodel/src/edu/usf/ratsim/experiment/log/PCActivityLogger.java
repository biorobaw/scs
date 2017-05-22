package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;
import java.util.Map;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;

public class PCActivityLogger extends Logger {

	PrintWriter writer = null;

	public PCActivityLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub) {
		if (writer == null)
			writer = getWriter();

		PlaceCellModel pcm = (PlaceCellModel) sub.getModel();
		Map<Integer, Float> activation = pcm.getPCActivity();
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episode = props.getProperty("episode");
		String cycle = props.getProperty("cycle");

		for (Integer cell : activation.keySet())
			writer.println(trialName + '\t' + groupName + '\t' + subName + '\t'
					+ episode + '\t' + cycle + '\t' + cell + '\t'
					+ activation.get(cell));
	}

	@Override
	public void log(Trial trial) {
		log(trial.getSubject());
	}

	@Override
	public void log(Episode episode) {
		log(episode.getSubject());
	}

	@Override
	public String getFileName() {
		return "cellactivity.csv";
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tcellNum\tactivation";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getSubject());
	}

	@Override
	public void finalizeLog() {
		if (writer != null)
			writer.close();
	}

}
