package edu.usf.experiment.universe;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

public interface GlobalCameraUniverse {

	// Robot position
	public abstract Point3f getRobotPosition();

	public abstract Quat4f getRobotOrientation();

	public abstract float getRobotOrientationAngle();
}
