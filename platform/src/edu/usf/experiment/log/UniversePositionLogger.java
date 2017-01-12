package edu.usf.experiment.log;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class UniversePositionLogger extends PositionLogger {

	public UniversePositionLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	public void log(Universe univ, Subject sub) {
		Point3f pos = univ.getRobotPosition();
		addPose(new Pose(pos.x, pos.y, false, sub.hasTriedToEat(), sub.hasEaten()));
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
	public String getFileName() {
		return "univposition.csv";
	}
	
	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\trandom\ttriedtoeat\tate";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse(), experiment.getSubject());		
	}

}

