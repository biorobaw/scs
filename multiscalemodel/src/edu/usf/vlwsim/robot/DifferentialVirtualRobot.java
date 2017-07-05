package edu.usf.vlwsim.robot;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.vlwsim.universe.DifferentialRobotVirtualUniverse;

/**
 * This robot implements differential control for the Virtual robot
 * @author martin
 *
 */
public class DifferentialVirtualRobot extends VirtualRobot implements DifferentialRobot {

	private DifferentialRobotVirtualUniverse universe;

	public DifferentialVirtualRobot(ElementWrapper params, Universe u) {
		super(params, u);
		
		this.universe = (DifferentialRobotVirtualUniverse) u;
	}
	
	@Override
	public void setLinearVel(float linearVel) {
		universe.setRobotV(linearVel);
	}

	@Override
	public void setAngularVel(float angularVel) {
		universe.setRobotW(angularVel);
	}

	@Override
	public void moveContinous(float lVel, float angVel) {
		universe.setRobotV(lVel);	
		universe.setRobotW(angVel);
	}

}
