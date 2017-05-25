package edu.usf.vlwsim.universe;

import javax.vecmath.Vector3f;

import edu.usf.experiment.utils.ElementWrapper;

public class AbsoluteDirectionRobotVirtualUniverse extends VirtUniverse {

	private float direction;
	private float step;

	public AbsoluteDirectionRobotVirtualUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		direction = 0;
	}

	@Override
	public void stepMotion() {
		setRobotOrientation(direction);
		moveRobot(new Vector3f(step, 0, 0));
	}

	public void setRobotNavDirection(float absoluteAngle) {
		direction = absoluteAngle;
	}

	public void setRobotADStep(float step) {
		this.step = step;
	}




}
