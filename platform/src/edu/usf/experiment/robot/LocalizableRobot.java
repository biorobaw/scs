package edu.usf.experiment.robot;

import com.vividsolutions.jts.geom.Coordinate;

public interface LocalizableRobot {

	/**
	 * Returns the position of the animal as thought by the subject
	 * @return
	 */
	public abstract Coordinate getPosition();
	
	/**
	 * Returns the orientation of the animal as thought by the subject
	 * @return
	 */
	public abstract float getOrientationAngle();
	
}
