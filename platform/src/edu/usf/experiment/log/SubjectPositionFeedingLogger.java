package edu.usf.experiment.log;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SubjectPositionFeedingLogger extends PositionFeedingLogger {

	public SubjectPositionFeedingLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Subject sub, Universe u){
		if (!(sub.getRobot() instanceof LocalizableRobot))
			throw new RuntimeException("SubjectPositionLogger needs a localizable robot to work");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		LocalizableRobot r = (LocalizableRobot) sub.getRobot();
		Point3f pos = r.getPosition();
		addPose(new Pose(pos.x, pos.y, false, fu.hasRobotTriedToEat(), fu.hasRobotEaten()));
	}
	
	@Override
	public void log(Trial trial) {
		log(trial.getSubject(), trial.getUniverse());
	}
	
	@Override
	public void log(Episode episode) {
		log(episode.getSubject(), episode.getUniverse());
	}

	@Override
	public String getFileName() {
		return "subjposition.csv";
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\trandom\ttriedToEat\tate";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getSubject(), experiment.getUniverse());		
	}

}
