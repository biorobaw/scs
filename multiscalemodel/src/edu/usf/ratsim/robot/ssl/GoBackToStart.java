package edu.usf.ratsim.robot.ssl;

import javax.vecmath.Point3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class GoBackToStart extends Task {

	private final float FEEDER_X = 550.07025f;
	private final float FEEDER_Y = -1200.7332f;
	private float init_rot_thrs;
	private float p_rot;
	private float dist_thrs;
	private float p_lin;
	private float final_rot_thrs;
	
	public GoBackToStart(ElementWrapper params) {
		super(params);
		
		p_lin = params.getChildFloat("p_lin");
		p_rot = params.getChildFloat("p_rot");
		dist_thrs = params.getChildFloat("dist_thrs");
		init_rot_thrs = params.getChildFloat("init_rot_thrs");
		final_rot_thrs = params.getChildFloat("final_rot_thrs");
	}
	
	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse(), experiment.getSubject().getRobot());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), trial.getSubject().getRobot());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), episode.getSubject().getRobot());
	}

	private void perform(Universe universe, Robot r) {
		goToPoint(FEEDER_X, FEEDER_Y, (float) (-Math.PI / 2), universe, r);
	}

	private void goToPoint(float x, float y, float t, Universe u, Robot r) {
		Point3f toP = new Point3f(x, y, 0);
		// Rotate to face
		float angleToGoal = GeomUtils.angleToPointWithOrientation(u.getRobotOrientation(), u.getRobotPosition(), toP);
		while (Math.abs(angleToGoal) > init_rot_thrs){
			angleToGoal = GeomUtils.angleToPointWithOrientation(u.getRobotOrientation(), u.getRobotPosition(), toP);
			r.moveContinous(0, p_rot * angleToGoal);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Go to point
		angleToGoal = GeomUtils.angleToPointWithOrientation(u.getRobotOrientation(), u.getRobotPosition(), toP);
		float distanceToGoal = toP.distance(u.getRobotPosition());
		while (distanceToGoal > dist_thrs){
			angleToGoal = GeomUtils.angleToPointWithOrientation(u.getRobotOrientation(), u.getRobotPosition(), toP);
			distanceToGoal = toP.distance(u.getRobotPosition());
			r.moveContinous(p_lin * distanceToGoal, p_rot * angleToGoal);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// Rotate to face desired orientation
		angleToGoal = GeomUtils.angleDiff(u.getRobotOrientationAngle(), t);
		while (Math.abs(angleToGoal) > final_rot_thrs){
			angleToGoal = GeomUtils.angleDiff(u.getRobotOrientationAngle(), t);
			r.moveContinous(0, p_rot * angleToGoal);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		r.moveContinous(0, 0);
	}
	
}
