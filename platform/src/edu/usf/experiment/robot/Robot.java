package edu.usf.experiment.robot;

/**
 * @author gtejera, mllofriu
 * 
 */
public interface Robot {
	
	/**
	 * Method invocked at the beginning of each session
	 */
	public abstract void startRobot();
	
	/**
	 * Returns the radius of the cylinder that wraps the robot
	 * @return the radius of the cyclinder that wraps the robot
	 */
	public float getRadius();

}
