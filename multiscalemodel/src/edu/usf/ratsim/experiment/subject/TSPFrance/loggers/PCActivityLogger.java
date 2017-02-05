package edu.usf.ratsim.experiment.subject.TSPFrance.loggers;

import java.io.PrintWriter;
import java.util.Map;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.TSPFrance.TSPSubjectFrance;

public class PCActivityLogger extends Logger {

	PrintWriter writer = null;

	public PCActivityLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(TSPSubjectFrance sub) {
		if (writer == null)
			writer = getWriter();

		Map<Integer, Float> activation = sub.getPCActivity();
//		PropertyHolder props = PropertyHolder.getInstance();
//		String trialName = props.getProperty("trial");
//		String groupName = props.getProperty("group");
//		String subName = props.getProperty("subject");
//		String episode = props.getProperty("episode");
//		String cycle = props.getProperty("cycle");
		
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episode = g.get("episode").toString();
		String cycle = g.get("cycle").toString();

		for (Integer cell : activation.keySet())
			writer.println(trialName + '\t' + groupName + '\t' + subName + '\t'
					+ episode + '\t' + cycle + '\t' + cell + '\t'
					+ activation.get(cell));
	}

	@Override
	public void log(Trial trial) {
		log((TSPSubjectFrance)trial.getSubject());
	}

	@Override
	public void log(Episode episode) {
		log((TSPSubjectFrance)episode.getSubject());
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
		log((TSPSubjectFrance)experiment.getSubject());
	}

	@Override
	public void finalizeLog() {
		if (writer != null)
			writer.close();
	}

}
