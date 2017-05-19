package edu.usf.experiment.universe;

import java.awt.geom.Point2D;

public interface MovableRobotUniverse extends Universe {

	/**
	 * Sets the virtual robot position
	 * 
	 * @param vector
	 *            Robots position
	 */
	public abstract void setRobotPosition(Point2D.Float float1, float w);
	
	/**
	 * Rotate the virtual world robot.
	 * 
	 * @param degrees
	 *            Amount to rotate in radians.
	 */
	public void rotateRobot(double degrees);
	
}
