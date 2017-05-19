package edu.usf.experiment.robot;

public interface StepRobot extends Robot {

	/**
	 * Move forward one step
	 */
	public abstract void forward(float distance);
	
	/**
	 * Makes the robot perform an action.
	 * 
	 * @param degrees
	 *            If degrees == 0, the robot goes forward. Else, it turns the
	 *            amount number of degrees. Negative degrees represent left
	 *            turns.
	 */
	public abstract void rotate(float degrees);
}
