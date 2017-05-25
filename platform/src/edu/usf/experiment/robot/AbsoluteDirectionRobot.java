package edu.usf.experiment.robot;

/**
 * A robot that is able to navigate in a certain direction expressed in an global frame of reference
 * @author martin
 *
 */
public interface AbsoluteDirectionRobot {

	public void setDirection(float absoluteAngle);
	
	public void setADStep(float step); 
}
