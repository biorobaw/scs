package edu.usf.experiment.robot;

import java.util.Set;

/**
 * A robot that requires calibrations before trials or episodes
 * @author David Ehrenhaft
 *
 */
public interface CalibratableRobot extends Robot{
	
	public void calibrate();

}
