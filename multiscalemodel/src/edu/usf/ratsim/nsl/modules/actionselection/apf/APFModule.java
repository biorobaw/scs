
package edu.usf.ratsim.nsl.modules.actionselection.apf;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Velocities;

public class APFModule extends Module {

	private static final float ANGULAR_P = .1f;

	private static final float LINEAR_P = .2f;

	private static final float REP_MULTIPLIER = .01f;

	private static final float ATTRACT_MAGNITUDE = .1f;

	private static final float MAX_REP_DIST = 0.2f;

	private HolonomicRobot r;

	public APFModule(String name, Robot robot) {
		super(name);

		this.r = (HolonomicRobot) robot;

	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		PointPort rPos = (PointPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		PointPort platPos = (PointPort) getInPort("platformPosition");

		// Start with the attractive field
		float relAngleToPlat = GeomUtils.relativeAngleToPoint(rPos.get(), rOrient.get(), platPos.get());
		Coordinate pGradient = new Coordinate((float) (Math.cos(relAngleToPlat) * ATTRACT_MAGNITUDE),
				(float) (Math.sin(relAngleToPlat) * ATTRACT_MAGNITUDE));
		// Add all repulsive fields, one per sensor
		for (int a = 0; a < angles.getSize(); a++) {
			float angle = angles.get(a);
			float reading = readings.get(a);
			float opposite = (float) (angle - Math.PI);
			float magnitude;
			if (reading < MAX_REP_DIST)
				magnitude = (float) (REP_MULTIPLIER * Math.pow(1 / reading - 1 / MAX_REP_DIST, 2));
			else
				magnitude = 0f;
			Coordinate rep = new Coordinate((float) Math.cos(opposite) * magnitude,
					(float) (Math.sin(opposite) * magnitude));
			pGradient = new Coordinate(pGradient.x + rep.x, pGradient.y + rep.y);
		}

		float angle = (float) Math.atan2(pGradient.y, pGradient.x);
		float magnitude = (float) pGradient.distance(new Coordinate());

		Velocities v = new Velocities();
		// Angular is proportional to angle difference
		v.theta = angle * ANGULAR_P;
		// If to close in the front of the robot, just rotate
		// float minDistFront = SonarUtils.getMinReading(readings, angles, 0f,
		// (float) (Math.PI/12));
		// if (minDistFront > CLOSE_THRS)
		v.x = magnitude * LINEAR_P;
		// else
		// v.linear = 0;
		// Enforce maximum velocities
		v.trim();

		// Execute commands
		r.setVels(v.x, v.y, v.theta);
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
