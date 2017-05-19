package edu.usf.experiment.universe;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

public interface GlobalCameraUniverse extends Universe {

	/**
	 * Return the virtual robot's position
	 * 
	 * @return
	 */
	public abstract Point3f getRobotPosition();

	public abstract Quat4f getRobotOrientation();

	public abstract float getRobotOrientationAngle();
}
