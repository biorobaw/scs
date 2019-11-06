package com.github.biorobaw.scs.robot.modules;


public interface DistanceSensingModule {
	
	/**
	 * Returns all distances measured by the sensor given by the id
	 * If id=-1, then all distances from all sensors should be returned
	 * @param id
	 * @return
	 */
	public abstract float[] getDistances(int id);
	
	/**
	 * Returns the closest distance measured and 
	 * the direction(s) in which it was (they were) measured.
	 * @return
	 */
	public abstract float[] getClosestDistance();
	
}
