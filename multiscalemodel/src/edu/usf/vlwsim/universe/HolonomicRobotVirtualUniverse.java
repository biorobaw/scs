package edu.usf.vlwsim.universe;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.utils.ElementWrapper;

public class HolonomicRobotVirtualUniverse extends VirtUniverse {
	
	/**
	 * Robot Velocities
	 */
	private float robotVx;
	private float robotVy;
	private float robotW;

	public HolonomicRobotVirtualUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		robotVx = robotVy = robotW = 0;
	}

	@Override
	public void stepMotion() {
		rotateRobot(robotW / 2 * getDeltaT());
		moveRobot(new Coordinate(robotVx * getDeltaT(), robotVy * getDeltaT()));
		rotateRobot(robotW / 2 * getDeltaT());
	}
	
	/**
	 * Set the robot velocities
	 * @param x
	 * @param y
	 * @param th
	 */
	public void setRobotVels(float x, float y, float th){
		robotVx = x;
		robotVy = y;
		robotW = th;
	}


}
