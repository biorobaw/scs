package com.github.biorobaw.scs.robot.modules.localization;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface SlamModule {

	/**
	 * Returns the position from the slam module
	 * @return
	 */
	public abstract Vector3D getPosition();
	
	
	/**
	 * Returns the orientation from the slam module
	 * @return
	 */
	public abstract Rotation getOrientation();
	
	/**
	 * Returns the orientation of the robot in 2D environments
	 * @return
	 */
	public abstract float getOrientation2D();
	
	
	default public String getDefaultName() {
		return "slam";
	};
}
