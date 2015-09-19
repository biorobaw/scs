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

public class ValueLogger extends Logger {

	private int numIntentions;
	private float angleInterval;
	private float interval;
	private boolean circle;
	private PrintWriter writer;

	public ValueLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		numIntentions = params.getChildInt("numIntentions");
		angleInterval = params.getChildFloat("angleInterval");
		interval = params.getChildFloat("interval");
		circle = params.getChildBoolean("circle");
		
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

		for (int intention = 0; intention < numIntentions; intention++) {
//			System.out.println("Logging intention " + intention);
			for (float xInc = 0; xInc < univ.getBoundingRectangle().getWidth()
					+ interval; xInc += interval) {
				for (float yInc = 0; yInc < univ.getBoundingRectangle()
						.getHeight() + interval; yInc += interval) {

					float x = (float) (univ.getBoundingRectangle().getMinX() + xInc);
					float y = (float) (univ.getBoundingRectangle().getMinY() + yInc);

					Point3f p = new Point3f(x, y, 0);
					float distToWall = univ.getDistanceToClosestWall(p);
					if (!circle
							|| inCircle(x, y, univ.getBoundingRectangle()
									.getWidth())) {

						Map<Float, Float> angleVals = sub.getValue(p,
								intention, angleInterval, distToWall);

						for (Float k : angleVals.keySet())
							writer.println(trialName + '\t' + groupName + '\t'
									+ subName + '\t' + episodeName + '\t' + cycle+ "\t" + x
									+ "\t" + y + "\t" + intention + "\t" + k
									+ "\t" + angleVals.get(k));
					}
					System.out.print(".");
				}
			}
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

	private boolean inCircle(float x, float y, double width) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) <= width / 2;
	}

	@Override
	public void finalizeLog() {
		writer.close();
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tcycle\tx\ty\tintention\tangle\tval";
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
