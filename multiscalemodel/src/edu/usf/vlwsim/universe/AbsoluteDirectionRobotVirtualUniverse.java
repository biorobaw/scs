package edu.usf.vlwsim.universe;

import com.vividsolutions.jts.geom.Coordinate;

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
		moveRobot(new Coordinate(step, 0));
	}

	public void setRobotNavDirection(float absoluteAngle) {
		direction = absoluteAngle;
	}

	public void setRobotADStep(float step) {
		this.step = step;
	}


}
