package edu.usf.experiment.log;

import java.io.PrintWriter;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class PolicyLogger extends Logger {

	private static final float MARGIN = 0.1f;
	private int numIntentions;
	private float angleInterval;
	private float interval;

	public PolicyLogger(ElementWrapper params, String logPath) {
		super(params, logPath);

		numIntentions = params.getChildInt("numIntentions");
		angleInterval = params.getChildFloat("angleInterval");
		interval = params.getChildFloat("interval");
	}

	public void log(Universe univ, SubjectOld sub) {
		PrintWriter writer = getWriter();
		
//		PropertyHolder props = PropertyHolder.getInstance();
//		String trialName = props.getProperty("trial");
//		String groupName = props.getProperty("group");
//		String subName = props.getProperty("subject");
//		String episodeName = props.getProperty("episode");
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		String episodeName = g.get("episode").toString();
		
		
		for (int intention = 0; intention < numIntentions; intention++) {
			for (float xInc = MARGIN; xInc
					- (univ.getBoundingRectangle().getWidth() - MARGIN / 2) < 1e-8; xInc += interval) {
				for (float yInc = MARGIN; yInc
						- (univ.getBoundingRectangle().getHeight() - MARGIN / 2) < 1e-8; yInc += interval) {
					float x = (float) (univ.getBoundingRectangle().getMinX() + xInc);
					float y = (float) (univ.getBoundingRectangle().getMinY() + yInc);

					float maxVal = Float.NEGATIVE_INFINITY;
					float bestAngle = 0;

					for (float angle = 0; angle <= 2 * Math.PI; angle += angleInterval) {
						Affordance a = sub.getHypotheticAction(new Point3f(x,
								y, 0), angle, intention);
						if (a.getValue() > maxVal){
//							System.out.println(a);
							maxVal = a.getValue();
							bestAngle = angle;
						}
					}

					String preferredAngleString = new Float(bestAngle)
							.toString();

					writer.println(trialName + '\t' + groupName + '\t' + subName
							+ '\t' + episodeName + '\t' + x + "\t" + y + "\t"
							+ intention + "\t" + preferredAngleString + "\t"
							+ maxVal);
				}
			}
		}
	}

	@Override
	public void finalizeLog() {

	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\tintention\theading\tval";
	}

	@Override
	public String getFileName() {
		return "policy.csv";
	}

	@Override
	public void log(Trial trial) {
		log(trial.getUniverse(), (SubjectOld)trial.getSubject());
	}
	
	@Override
	public void log(Episode episode) {
		log(episode.getUniverse(), (SubjectOld)episode.getSubject());
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse(), (SubjectOld)experiment.getSubject());
	}

}
