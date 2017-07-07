package edu.usf.experiment.universe.feeder;

import java.util.List;

import edu.usf.experiment.universe.Universe;

public interface FeederUniverse extends Universe {

	// Insertion and Deletion
	public void addFeeder(int id, float x, float y);

	public Feeder getFeeder(int i);

	public List<Feeder> getFeeders();
	
	public float getCloseThrs();

	// Modifiers
	public void setActiveFeeder(int i, boolean val);

	public void setFlashingFeeder(int i, boolean flashing);

	public void setEnableFeeder(Integer f, boolean enabled);

	public void setPermanentFeeder(Integer id, boolean b);

	public void releaseFood(int feeder);

	public void clearFoodFromFeeder(Integer f);
	
	// Simulation methods
	/**
	 * Allow the robot to execute the action of eating. Most robots don't
	 * implement real actions for eating, so it must be dealt with function
	 * calls
	 */
	public void robotEat();
	
	/**
	 * Whether the robot has eaten in the last cycle
	 * 
	 * @return
	 */
	public boolean hasRobotEaten();

	/**
	 * Whether the robot has tried to eat in the last cycle
	 * 
	 * @return
	 */
	public boolean hasRobotTriedToEat();
	
	// Display methods
	/**
	 * Inform the universe of the currently pursued feeder for displaying
	 * purposes
	 * 
	 * @param feeder
	 *            which feeder is being pursued
	 * @param wanted
	 *            whether is being pursued or not
	 */
//	public void setWantedFeeder(int feeder, boolean wanted);

	
}
