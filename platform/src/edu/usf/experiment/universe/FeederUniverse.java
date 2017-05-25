package edu.usf.experiment.universe;

import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.LineSegment;

public interface FeederUniverse extends Universe {



	// Feeders
	public Point3f getFoodPosition(int i);
	
	public List<Integer> getFlashingFeeders();

	public List<Integer> getActiveFeeders();

	public int getNumFeeders();
	
	public void setActiveFeeder(int i, boolean val);

	public void setFlashingFeeder(int i, boolean flashing);
	
	public List<Integer> getFeederNums();

	public List<Feeder> getFeeders();

	public Feeder getFeeder(int i);

	public boolean isFeederActive(int feeder);

	public boolean isFeederFlashing(int feeder);

	public void releaseFood(int feeder);

	public boolean hasFoodFeeder(int feeder);

	// Involving position and food
	public boolean hasRobotFoundFood();

	public void robotEat();

	public int getLastFeedingFeeder();

	public boolean isRobotCloseToFeeder(int currentGoal);

	public int getFeedingFeeder();

	public boolean hasRobotFoundFeeder(int i);

	public boolean isRobotCloseToAFeeder();

	public float getDistanceToFeeder(int i);
	
	public int getFoundFeeder();

	public List<Integer> getEnabledFeeders();

	public void setEnableFeeder(Integer f, boolean enabled);
	
	public boolean isFeederEnabled(int feeder);
	
	public float shortestDistanceToFeeders(LineSegment wall);

	public void clearFoodFromFeeder(Integer f);

	public void addFeeder(int id, float x, float y);

	public void addFeeder(Feeder f);
	
	public void setPermanentFeeder(Integer id, boolean b);
	
	/**
	 * Inform the universe of the currently pursued feeder for displaying purposes
	 * @param feeder which feeder is being pursued
	 * @param wanted whether is being pursued or not
	 */
	public void setWantedFeeder(int feeder, boolean wanted);
	
	public float wallDistanceToFeeders(LineSegment wall);

	/**
	 * Whether the robot has eaten in the last cycle
	 * @return
	 */
	public boolean hasRobotEaten();

	/**
	 * Whether the robot has tried to eat in the last cycle
	 * @return
	 */
	public boolean hasRobotTriedToEat();
}
