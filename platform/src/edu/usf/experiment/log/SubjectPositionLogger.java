package edu.usf.experiment.log;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class SubjectPositionLogger extends PositionLogger {

	public SubjectPositionLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void log(Universe u, Subject sub){
		if (!(sub.getRobot() instanceof LocalizableRobot))
			throw new RuntimeException("SubjectPositionLogger needs a localizable robot to work");
		
		LocalizableRobot r = (LocalizableRobot) sub.getRobot();
		Coordinate pos = r.getPosition();
		addPose(new Coordinate(pos.x, pos.y));
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
