package edu.usf.experiment.robot;

public interface SonarRobot {

	/**
	 * Get the current readings for all sonar sensors
	 * @return An array with the current readings for the sonar sensors
	 */
	public float[] getSonarReadings();
	
	/**
	 * Get the angles of the sonar arrays
	 * @return An array specifying the angle (rads) each sonar is pointing to in the robot's
	 * frame of reference
	 */
	public float[] getSonarAngles();
	
	/**
	 * Get the aperture angle of sonar sensors
	 * @return The aperture angle of every sonar sensor in radians
	 */
	public float getSonarAperture();	// TODO: remove this from interface?
	
}
