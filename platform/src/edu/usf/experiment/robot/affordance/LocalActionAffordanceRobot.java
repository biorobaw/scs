package edu.usf.experiment.robot.affordance;

public interface LocalActionAffordanceRobot extends AffordanceRobot {

	public abstract float getMinAngle();

	public abstract float getStepLength();

	public abstract Affordance getForwardAffordance();

	public abstract Affordance getLeftAffordance();

	public abstract Affordance getRightAffordance();
}
