package edu.usf.experiment.robot;

import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class LocalizableRobot extends Robot {

	public LocalizableRobot(ElementWrapper params) {
		super(params);
	}

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
	
	/**
	 * Returns the orientation of the animal as thought by the subject
	 * @return
	 */
	public abstract Quat4f getOrientation();

	public abstract boolean seesFeeder();
	
	/**
	 * Return a list with all universe feeders for place cell layout purposes
	 * @return
	 */
	public abstract List<Feeder> getAllFeeders();

	public abstract float getDistanceToClosestWall();

	public abstract float getHalfFieldView();

	public abstract int closeToNoseWalls(float distToConsider);
}
