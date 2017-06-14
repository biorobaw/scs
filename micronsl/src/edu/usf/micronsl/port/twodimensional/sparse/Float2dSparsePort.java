package edu.usf.micronsl.port.twodimensional.sparse;

import java.util.Map;

import edu.usf.micronsl.module.Module;
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
	public abstract Map<Entry, Float> getNonZero();
	
	/**
	 * Get all the non zero elements for a certain row
	 * @return
	 */
	public abstract Map<Entry, Float> getNonZeroRow(int row);

	/**
	 * Returns whether row i has non-zero elements
	 * @param i
	 * @return
	 */
	public abstract boolean isRowEmpty(int i);
	
}
