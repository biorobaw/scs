package edu.usf.ratsim.robot.ssl;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;

public class GoBackToStart extends Task {
	private static final float P1_X = -1.0f;
	private static final float P1_Y =  0.027618423f;
	private static final float P2_X = -0.019091368f;
	private static final float P2_Y = -0.9812125f;
	private final float FEEDER_X = -0.47929716f;
	private final float FEEDER_Y =  -0.50927526f;
	private final float START_X = 0.8288059f;
	private final float START_Y = 0.78242755f;
	private final float START_T = -1.4741312f;
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
		perform(experiment.getUniverse(), (RobotOld)experiment.getSubject().getRobot());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), (RobotOld)trial.getSubject().getRobot());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), (RobotOld)episode.getSubject().getRobot());
	}

	private void perform(Universe u, RobotOld r) {
		Point3f robot = u.getRobotPosition();
		Point3f feeder = new Point3f(FEEDER_X, FEEDER_Y, 0);
		Point3f start = new Point3f(START_X, START_Y, 0);
		// If farther away from the start than feeder -> behind feeder
		if (start.distance(robot) > start.distance(feeder)) {
			Point2f rInFeederFrame = new Point2f(robot.x - feeder.x, robot.y - feeder.y);
			// If x is larger, closer to y (negative coords)
			if (rInFeederFrame.x < rInFeederFrame.y) {
				goToPoint(P1_X, P1_Y, 0, u, r);
			} else {
				goToPoint(P2_X, P2_Y, 0, u, r);
			}
		}
		goToPoint(START_X, START_Y, START_T, u, r);
	}

	private void goToPoint(float x, float y, float t, Universe u, RobotOld r) {
		Point3f toP = new Point3f(x, y, 0);
		// Rotate to face
		float angleToGoal = angleToPwithO(u.getRobotOrientation(), u.getRobotPosition(), toP);
		while (Math.abs(angleToGoal) > init_rot_thrs) {
			// If we are not receiving cam info, wait until we do
			if (((GlobalCameraUniv) u).isCamInfoFresh()) {
				angleToGoal = angleToPwithO(u.getRobotOrientation(), u.getRobotPosition(), toP);
//				System.out.println(angleToGoal);
				r.moveContinous(0, -p_rot * angleToGoal);
			} else {
				r.moveContinous(0, 0);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Go to point
		angleToGoal = angleToPwithO(u.getRobotOrientation(), u.getRobotPosition(), toP);
		float distanceToGoal = toP.distance(u.getRobotPosition());
		while (distanceToGoal > dist_thrs) {
			// If we are not receiving cam info, wait until we do
			if (((GlobalCameraUniv) u).isCamInfoFresh()) {
				angleToGoal = angleToPwithO(u.getRobotOrientation(), u.getRobotPosition(), toP);
				distanceToGoal = toP.distance(u.getRobotPosition());
				r.moveContinous(p_lin * distanceToGoal, -p_rot * angleToGoal);
			} else {
				r.moveContinous(0, 0);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Rotate to face desired orientation
		angleToGoal = GeomUtils.angleDiff(u.getRobotOrientationAngle(), t);
		while (Math.abs(angleToGoal) > final_rot_thrs) {
			// If we are not receiving cam info, wait until we do
			if (((GlobalCameraUniv) u).isCamInfoFresh()) {
				angleToGoal = GeomUtils.angleDiff(u.getRobotOrientationAngle(), t);
				r.moveContinous(0, p_rot * angleToGoal);
			} else {
				r.moveContinous(0, 0);
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		r.moveContinous(0, 0);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private float angleToPwithO(Quat4f orientation, Point3f from, Point3f to) {
		Vector3f toPoint = GeomUtils.pointsToVector(from, to);
		Quat4f rotTo = GeomUtils.rotBetweenVectors(new Vector3f(1, 0, 0), toPoint);
		rotTo.inverse();
		rotTo.mul(orientation);
		rotTo.normalize();
		float angle = (float) (2 * Math.acos(rotTo.w)) * Math.signum(rotTo.z);
		return angle;
	}
}
