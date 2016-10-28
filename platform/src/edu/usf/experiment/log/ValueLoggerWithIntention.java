package edu.usf.experiment.log;

import java.io.PrintWriter;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class ValueLoggerWithIntention extends Logger {

	private PrintWriter writer;
	private int numIntentions;

	public ValueLoggerWithIntention(ElementWrapper params, String logPath) {
		super(params, logPath);

		numIntentions = params.getChildInt("numIntentions");
		
		writer = getWriter();
	}

	public void log(Universe univ, Subject sub) {
		PropertyHolder props = PropertyHolder.getInstance();
		String trialName = props.getProperty("trial");
		String groupName = props.getProperty("group");
		String subName = props.getProperty("subject");
		String episodeName = props.getProperty("episode");
		String cycle = props.getProperty("cycle");

		System.out.println("Starting to log value");


		for (int intention = 0; intention < numIntentions; intention++){
			Map<Point3f, Float> valPoints = sub.getValuePoints(intention);
	
			for (Point3f p : valPoints.keySet())
				writer.println(trialName + '\t' + groupName + '\t'
						+ subName + '\t' + episodeName + '\t' + cycle+ "\t" + p.x
						+ "\t" + p.y + '\t' +  intention + '\t' +  valPoints.get(p));
		}

		System.out.println("Finished loggin value");
	}

	@Override
	public void log(Trial trial) {
		log(trial.getUniverse(), trial.getSubject());
	}

	@Override
	public void log(Episode episode) {
		log(episode.getUniverse(), episode.getSubject());
	}


	@Override
	public void finalizeLog() {
		writer.close();
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tx\ty\tintention\tval";
	}

	@Override
	public String getFileName() {
		return "value.csv";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse(), experiment.getSubject());
	}

}
