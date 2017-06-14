package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;
import java.util.Map;


import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.ratsim.experiment.subject.interfaces.ActivityLoggerSubject;

public class PCActivityLogger extends Logger {

	PrintWriter writer = null;

	public PCActivityLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}


	
	public void log(Subject sub) {
				
		ActivityLoggerSubject als = null;
		
		if( sub instanceof ActivityLoggerSubject ) als = (ActivityLoggerSubject)sub;
		else {
			System.out.println("ERROR - SUBJECT DOESNT IMPLEMENT ACTIVITY LOGGER SUBJECT INTERFACE");
			System.exit(0);
		}
		
		if (writer == null)
			writer = getWriter();

		Map<Integer, Float> activation = als.getPCActivity();
		
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
