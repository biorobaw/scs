package edu.usf.vlwsim.universe;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.utils.ElementWrapper;

public class DifferentialRobotVirtualUniverse extends VirtUniverse {
	
	/**
	 * Robot Velocities
	 */
	private float robotV;
	private float robotW;

	public DifferentialRobotVirtualUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		robotV = robotW = 0;
	}

	@Override
	public void stepMotion() {
		rotateRobot(robotW / 2 * getDeltaT());
		moveRobot(new Coordinate(robotV * getDeltaT(), 0));
		rotateRobot(robotW / 2 * getDeltaT());
	}
	
	/**
	 * Sets the robot linear velocity
	 * @param v the linear velocity in m/s
	 */
	public void setRobotV(float v) {
		robotV = v;
	}

	/**
	 * Set the robot angular velocity
	 * @param w the angular velocity
	 */
	public void setRobotW(float w) {
		robotW = w;
	}


}
