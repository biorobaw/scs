package edu.usf.experiment.universe;

import com.vividsolutions.jts.geom.Coordinate;

public interface MovableRobotUniverse {

	/**
	 * Sets the virtual robot position
	 * 
	 * @param vector
	 *            Robots position
	 */
	public abstract void setRobotPosition(Coordinate snewPos);
	
	/**
	 * Rotate the virtual world robot.
	 * 
	 * @param degrees
	 *            Amount to rotate in radians.
	 */
	public void setRobotOrientation(float degrees);
	
}
