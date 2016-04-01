package edu.usf.micronsl.port.onedimensional.sparse;

import java.util.Map;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * A port that is able to return the non-zero elements stored
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Float1dSparsePort extends Float1dPort {

	public Float1dSparsePort(Module owner) {
		super(owner);
	}

	/**
	 * Get the non-zero elements in the port
	 * 
	 * @return A map from the index of the element to the value stored
	 */
	public abstract Map<Integer, Float> getNonZero();

	/**
	 * Get the expected proportion of non-zero elements
	 * 
	 * @return The expected number of non-zero elements
	 */
	public abstract float getSparseness();

}
