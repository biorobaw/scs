package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.FeedingLog;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.log.Pose;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.MultiScaleArtificialPCSubject;

public class PCActivityLogger extends Logger {

	PrintWriter writer = null;

	public PCActivityLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub) {
		if (writer == null)
			writer = getWriter();

		List<float[]> activation = ((MultiScaleArtificialPCSubject) sub)
				.getCellActivity();
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episode = props.getProperty("episode");
		String cycle = props.getProperty("cycle");

		int cellNum = 0;
		for (float[] layerActivity : activation)
			for (float cellActivation : layerActivity) {
				if (cellActivation > 0) {
					writer.println(trialName + '\t' + groupName + '\t'
							+ subName + '\t' + episode + '\t' + cycle + '\t'
							+ cellNum + '\t' + cellActivation);
				}
				cellNum++;
			}
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
