package edu.usf.ratsim.nsl.modules.pathplanning;

import javax.vecmath.Point3f;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.support.SonarUtils;

public class PointNode {

	// TODO: make these parameters
	private static final float MAX_RADIUS = .5f;

	public Point3f prefLoc;
	public float activation;

	public PointNode(Point3f prefLoc) {
		this.prefLoc = prefLoc;
	}

	/**
	 * Updates the activation value of the node
	 * 
	 * @param rPos
	 *            the position of the robot
	 * @param sonarReadings
	 *            the sonar sensor readings
	 * @param sonarAngles
	 *            the angles of the sonar sensors in the robot frame of
	 *            reference
	 * @param orientation
	 */
	public void updateActivation(Point3f rPos, float orientation, Float1dPort sonarReadings, Float1dPort sonarAngles) {
		float angle = -GeomUtils.angleToPointWithOrientation(orientation, rPos, prefLoc);

		// No good sensor for the angle, or obstacle closer than the unit's
		// center
		float dist = prefLoc.distance(rPos);
		if (!SonarUtils.validSonar(angle, sonarReadings, sonarAngles)
				|| SonarUtils.getReading(angle, sonarReadings, sonarAngles) < dist)
			activation = 0;
		else {
			if (dist > MAX_RADIUS)
				activation = 0;
			else
				activation = (float) Math.exp(-Math.pow(dist, 2));
		}

	}

	public void updateActivation(Point3f loc, float distToObs) {

	}

	public float getActivation() {
		return activation;
	}

	public String toString() {
		return "V" + prefLoc.toString();
	}

}