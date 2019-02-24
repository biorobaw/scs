package edu.usf.experiment.universe;

import com.vividsolutions.jts.geom.Coordinate;

public interface GlobalCameraUniverse  {

	/**
	 * Return the virtual robot's position
	 * 
	 * @return
	 */
	public abstract Coordinate getRobotPosition();

	public abstract float getRobotOrientationAngle();
}