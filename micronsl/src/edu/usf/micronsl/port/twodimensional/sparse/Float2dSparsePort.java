package edu.usf.micronsl.port.twodimensional.sparse;

import java.util.Map;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

/**
 * A port that is able to return the non-zero elements stored
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Float2dSparsePort extends Float2dPort {

	public Float2dSparsePort(Module owner) {
		super(owner);
	}

	/**
	 * Get the non-zero elements in the port
	 * 
	 * @return A map from the index of the element to the value stored
	 */
	public abstract Map<Integer, Map<Integer, Float>> getNonZeroRows();
	
	public abstract Map<Integer, Float> getNonZeroRowElements();
	
	/**
	 * Get the expected proportion of non-zero elements
	 * 
	 * @return The expected number of non-zero elements
	 */
	public abstract float getSparseness();



}
