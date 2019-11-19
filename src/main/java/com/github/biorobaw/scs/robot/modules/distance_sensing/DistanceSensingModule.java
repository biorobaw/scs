package com.github.biorobaw.scs.robot.modules.distance_sensing;


public interface DistanceSensingModule {
	
	/**
	 * Returns the distance measured by the sensor given by the id
	 * @param id
	 * @return
	 */
	public abstract float getDistances(int id);
	
	
	/**
	 * Returns all distances measured by the distance sensor of this module
	 * @return
	 */
	public abstract float[] getDistances();
	
	
	default public String getDefaultName() {
		return "distance_sensors";
	}
}
