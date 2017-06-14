package edu.usf.ratsim.experiment.log;

import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.log.Logger;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class ValueEntropyLogger extends Logger {

	private PrintWriter writer;

	public ValueEntropyLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		writer = getWriter();
	}

	public void log(Universe univ, SubjectOld sub) {
//		PropertyHolder props = PropertyHolder.getInstance();
//		String trialName = props.getProperty("trial");
//		String groupName = props.getProperty("group");
//		String subName = props.getProperty("subject");
//		String episodeName = props.getProperty("episode");
//		String cycle = props.getProperty("cycle");
		
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episodeName = g.get("episode").toString();
		String cycle = g.get("cycle").toString();

//		System.out.println("Starting to log value");
		
		float valueEntropy = sub.getValueEntropy();

		writer.println(trialName + '\t' + groupName + '\t' + subName + '\t'
				+ episodeName + '\t' + cycle + "\t" + valueEntropy);

//		System.out.println("Finished loggin value");
	}

	@Override
	public void log(Trial trial) {
		log(trial.getUniverse(), (SubjectOld)trial.getSubject());
	}

	@Override
	public void log(Episode episode) {
		log(episode.getUniverse(), (SubjectOld)episode.getSubject());
	}

	private boolean inCircle(float x, float y, double width) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) <= width / 2;
	}

	@Override
	public void finalizeLog() {
		writer.close();
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tentropy";
	}

	@Override
	public String getFileName() {
		return "valueEntropy.csv";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse(), (SubjectOld)experiment.getSubject());
	}

}
