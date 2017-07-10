package edu.usf.ratsim.nsl.modules.pathplanning;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.support.SonarUtils;

public class PointNode {

	public Coordinate prefLoc;
	public boolean following;
	public float distToRobot;

	public PointNode(Coordinate prefLoc) {
		this.prefLoc = prefLoc;
		this.following = false;
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
	public void updateDistance(Coordinate rPos, float orientation, Float1dPort sonarReadings,
			Float1dPort sonarAngles) {
		float angle = GeomUtils.relativeAngleToPoint(rPos, orientation, prefLoc);

		distToRobot = (float) prefLoc.distance(rPos);
		// No good sensor for the angle, or obstacle closer than the unit's
		// center or too far
		if (!SonarUtils.validSonar(angle, sonarReadings, sonarAngles)
				|| SonarUtils.getReading(angle, sonarReadings, sonarAngles) < distToRobot) 
			distToRobot = Float.MAX_VALUE;

	}

	public void updateActivation(Coordinate loc, float distToObs) {

	}

	public String toString() {
		return "V" + prefLoc.toString();
	}

}