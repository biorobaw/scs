package edu.usf.experiment.robot;

public interface DifferentialRobot  {

	/**
	 * Sets the linear velocity of the robot
	 * @param linearVel the new linear velocity, in m/s.
	 */
	public void setLinearVel(float linearVel);
	
	/**
	 * Sets the angular velocity of the robot
	 * @param angularVel the new angular velocity, in radians.
	 */
	public void setAngularVel(float angularVel);
	
	/**
	 * Move the robot in a continous fashion, as opposed to step motions
	 * @param lVel Linear velocity, positive values produce forward motions
	 * @param angVel Angular velocity, positive values mean left rotations
	 */
	public abstract void moveContinous(float lVel, float angVel);
}
