package edu.usf.experiment.robot;

import java.util.List;

import edu.usf.experiment.universe.Feeder;

public interface FeederRobot extends VisionRobot {

	/**
	 * Makes the robot eat food
	 */
	public void eat();

	/**
	 * Return whether the robot has found food in the environment
	 * 
	 * @return
	 */
	public boolean hasFoundFood();
	
	public Feeder getFlashingFeeder();

	public boolean seesFlashingFeeder();

	public boolean isFeederClose();
	
	/**
	 * Returns the feeders visible to the robot
	 * @return
	 */
	public List<Feeder> getVisibleFeeders();
	
	public int getLastAteFeeder();

	public int getLastTriedToEatFeeder() ;

	public Feeder getFeederInFront();

	/**
	 * Bookeeping method to signal the robot ate: last action was a successful eat
	 * @return
	 */
	public boolean hasRobotEaten();

	/**
	 * Bookeeping method to signal the robot tried to eat: last action was eating
	 * @return
	 */
	public boolean hasRobotTriedToEat();

	public List<Feeder> getAllFeeders();

	public void clearEaten();

	/**
	 * Function to set that the robot has successfully eaten
	 * Used by the universe as a callback to inform the robot that the eat action was successful
	 */
	public void setAte();
}
