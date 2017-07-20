package edu.usf.vlwsim.robot;

import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.vlwsim.universe.HolonomicRobotVirtualUniverse;

/**
 * This robot implements differential control for the Virtual robot
 * @author martin
 *
 */
public class HolonomicVirtualRobot extends VirtualRobot implements HolonomicRobot {

	private HolonomicRobotVirtualUniverse universe;

	public HolonomicVirtualRobot(ElementWrapper params, Universe u) {
		super(params, u);
		
		this.universe = (HolonomicRobotVirtualUniverse) u;
	}

	@Override
	public void setVels(float x, float y, float th) {
		universe.setRobotVels(x, y, th);
		System.out.println(x + " " + y + " " + th);
	}
	
}
