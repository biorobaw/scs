package edu.usf.micronsl.twodimensional;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.Port;

/**
 * A port that holds float values indexables by two indices
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Float2dPort extends Port {

	/**
	 * Create a port
	 * 
	 * @param owner
	 *            Thew owner module
	 */
	public Float2dPort(Module owner) {
		super(owner);
	}

	/**
	 * Get the number of rows
	 * 
	 * @return The number of rows
	 */
	public abstract int getNRows();

	/**
	 * Get the number of columns
	 * 
	 * @return The number of columns
	 */
	public abstract int getNCols();

	/**
	 * Get the element at position (i,j)
	 * 
	 * @param i
	 *            The row index
	 * @param j
	 *            The column index
	 * @return
	 */
	public abstract float get(int i, int j);

	/**
	 * Set the element at position (i,j) to value x
	 * 
	 * @param i
	 *            The row index
	 * @param j
	 *            The column index
	 * @param x
	 *            The desired value
	 */
	public abstract void set(int i, int j, float x);

	@Override
	public int getSize() {
		return getNRows() * getNCols();
	}

	/**
	 * Returns the 2d array used to hold the data.
	 * 
	 * @return The internal structure used to hold the data. The obtained array
	 *         is not of exclusive access and may be modified in the future.
	 */
	public abstract float[][] getData();

	/**
	 * Returns the 2d array in data.
	 * 
	 * @return
	 * 
	 * @return The internal structure used to hold the data. The obtained array
	 *         is a copy of the existing data.
	 */
	public abstract void getData(float[][] data);

}
