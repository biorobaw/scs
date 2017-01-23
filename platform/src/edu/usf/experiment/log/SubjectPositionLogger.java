package edu.usf.experiment.log;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.utils.ElementWrapper;

public class SubjectPositionLogger extends PositionLogger {

	public SubjectPositionLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(SubjectOld sub) {
		if (!(sub.getRobot() instanceof LocalizableRobot))
			throw new RuntimeException("SubjectPositionLogger needs a localizable robot to work");
		
		LocalizableRobot r = (LocalizableRobot) sub.getRobot();
		Point3f pos = r.getPosition();
		addPose(new Pose(pos.x, pos.y, false, sub.hasTriedToEat(), sub.hasEaten()));
	}
	
	@Override
	public void log(Trial trial) {
		log((SubjectOld)trial.getSubject());
	}
	
	@Override
	public void log(Episode episode) {
		log((SubjectOld)episode.getSubject());
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
		log((SubjectOld)experiment.getSubject());		
	}

}
