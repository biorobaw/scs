package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.micronsl.port.onedimensional.Float1dPort;

public interface PlaceCellLayer {

	public Map<Integer, Float> getActive(Point3f position);
	
	public Float1dPort getActivationPort();

	/**
	 * Returns the maximum sum of activation values for place cells in any give location 
	 * @return
	 */
	public float getMaxActivation();
	
}
