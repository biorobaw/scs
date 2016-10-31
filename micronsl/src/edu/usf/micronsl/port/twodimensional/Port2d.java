package edu.usf.micronsl.port.twodimensional;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

/**
 * A port that holds float values indexables by two indices
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Port2d <t extends Number> extends Port {

	/**
	 * Create a port
	 * 
	 * @param owner
	 *            Thew owner module
	 */
	public Port2d(Module owner) {
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
	public abstract t get(int i, int j);

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
	public abstract void set(int i, int j, t x);

	@Override
	public int getSize() {
		return getNRows() * getNCols();
	}


	
	
	public abstract Object getData();

	/**
	 * Returns the 2d array in data.
	 * 
	 * @return
	 * 
	 * @return The internal structure used to hold the data. The obtained array
	 *         is a copy of the existing data.
	 */
	public abstract void getDataArrayMatrix(t[][] data);

}
