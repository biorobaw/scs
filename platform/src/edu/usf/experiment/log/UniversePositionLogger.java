package edu.usf.experiment.log;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class UniversePositionLogger extends PositionLogger {

	public UniversePositionLogger(ElementWrapper params, String logPath) {
		super(params, logPath);
		// TODO Auto-generated constructor stub
	}

	public void log(Universe univ) {
		Point3f pos = univ.getRobotPosition();
		addPose(new Pose(pos.x, pos.y, false, false, false));
	}
	
	@Override
	public void log(Trial trial) {
		log(trial.getUniverse());
	}
	
	@Override
	public void log(Episode episode) {
		log(episode.getUniverse());
	}

	@Override
	public String getFileName() {
		return "univposition.csv";
	}
	
	@Override
	public String getHeader() {
		return "trial\tgroup\tsubject\trepetition\tx\ty\trandom";
	}

	@Override
	public void log(Experiment experiment) {
		log(experiment.getUniverse());		
	}

}

