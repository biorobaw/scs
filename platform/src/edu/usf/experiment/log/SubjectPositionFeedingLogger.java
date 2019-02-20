package edu.usf.experiment.log;

import com.vividsolutions.jts.geom.Coordinate;

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

	@Override
	public void log(Universe u, Subject sub){
		if (!(sub.getRobot() instanceof LocalizableRobot))
			throw new RuntimeException("SubjectPositionLogger needs a localizable robot to work");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		LocalizableRobot r = (LocalizableRobot) sub.getRobot();
		Coordinate pos = r.getPosition();
		addPose(new Pose((float)pos.x,(float) pos.y, false, fu.hasRobotTriedToEat(), fu.hasRobotEaten()));
	}
	

	@Override
	public String getFileName() {
		return "subjposition.csv";
	}

	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\trandom\ttriedToEat\tate";
	}


}
