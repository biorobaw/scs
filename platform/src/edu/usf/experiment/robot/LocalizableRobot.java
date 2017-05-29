package edu.usf.experiment.robot;

import javax.vecmath.Point3f;

public interface LocalizableRobot {

	/**
	 * Returns the position of the animal as thought by the subject
	 * @return
	 */
	public abstract Point3f getPosition();
	
	/**
	 * Returns the orientation of the animal as thought by the subject
	 * @return
	 */
	public abstract float getOrientationAngle();
	
}
