package edu.usf.experiment.universe;

import edu.usf.experiment.robot.Robot;

public interface Universe {

	/**
	 * Make a step in simulation/real world. The amount of time is up to the universe.
	 */
	public void step();

	/**
	 * Set a reference to the robot. Used to give the robot feedback about its
	 * actions, when it is not able to deduce it by itself.
	 * 
	 * @param robot
	 */
	public void setRobot(Robot robot);

}
