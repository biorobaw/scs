
package edu.usf.ratsim.nsl.modules.actionselection.apf;

import java.awt.geom.Point2D;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Velocities;

public class APFModule extends Module {

	private static final float MIN_DIST_TO_OBS = 0.1f;

	private static final float MIN_DIST_TO_OBS_ROT = 0.3f;

	private static final float REP_ANGULAR_P = 1f;

	private static final float ANGULAR_P = .4f;

	private static final float LINEAR_P = .2f;

	private static final float REP_LINEAR_P = .3f;

	private static final float CLOSE_THRS = .15f;

	private static final float MAX_READ = .3f;

	private static final float REP_MULTIPLIER = 10f;

	private static final float ATTRACT_MAGNITUDE = .2f;

	private DifferentialRobot r;

	public APFModule(String name, Robot robot) {
		super(name);

		this.r = (DifferentialRobot) robot;

	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		// Start with the attractive field
		float relAngleToPlat = -GeomUtils.angleToPointWithOrientation(rOrient.get(), rPos.get(), platPos.get());
		Point2D.Float pGradient = new Point2D.Float((float) (Math.cos(relAngleToPlat) * ATTRACT_MAGNITUDE),
				(float) (Math.sin(relAngleToPlat) * ATTRACT_MAGNITUDE));
		// Add all repulsive fields, one per sensor
		for (int a = 0; a < angles.getSize(); a++) {
			float angle = angles.get(a);
			float reading = readings.get(a);
			float opposite = (float) (angle - Math.PI);
			float magnitude = (float) Math.exp(REP_MULTIPLIER * Math.pow(MAX_READ - reading, 2)) - 1;
			Point2D.Float rep = new Point2D.Float((float) Math.cos(opposite) * magnitude,
					(float) (Math.sin(opposite) * magnitude));
			pGradient = new Point2D.Float((float) (pGradient.getX() + rep.getX()),
					(float) (pGradient.getY() + rep.getY()));
		}

		float angle = (float) Math.atan2(pGradient.getY(), pGradient.getX());
		float magnitude = (float) pGradient.distance(0, 0);

		Velocities v = new Velocities();
		// Angular is proportional to angle difference
		v.angular = angle * ANGULAR_P;
		// If to close in the front of the robot, just rotate
		// float minDistFront = SonarUtils.getMinReading(readings, angles, 0f,
		// (float) (Math.PI/12));
		// if (minDistFront > CLOSE_THRS)
		v.linear = magnitude * LINEAR_P;
		// else
		// v.linear = 0;
		// Enforce maximum velocities
		v.trim();

		// Execute commands
		r.setAngularVel(v.angular);
		r.setLinearVel(v.linear);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void newEpisode() {
		super.newEpisode();

	}

}
