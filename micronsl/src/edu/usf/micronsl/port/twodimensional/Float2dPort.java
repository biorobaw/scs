package edu.usf.micronsl.port.twodimensional;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * A port that holds float values indexables by two indices
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Float2dPort extends Float1dPort {

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
	public abstract float[][] get2dData();

	/**
	 * Returns the 2d array in data.
	 * 
	 * @return
	 * 
	 * @return The internal structure used to hold the data. The obtained array
	 *         is a copy of the existing data.
	 */
	public abstract void get2dData(float[][] data);

	
	@Override
	public float get(int index) {
		// TODO Auto-generated method stub
		return get(index /getNRows(),index % getNCols());
	}

	@Override
	public void set(int i, float x) {
		// TODO Auto-generated method stub
		set(i /getNRows(),i % getNCols(),x);
	}

	@Override
	public float[] getData() {
		// TODO Auto-generated method stub
		new NullPointerException().printStackTrace();
		System.out.println("Unimplemented for 2d Arrays");
		System.exit(-1);
		return null;
	}

	@Override
	public void getData(float[] data) {
		// TODO Auto-generated method stub
		new NullPointerException().printStackTrace();
		System.out.println("Unimplemented for 2d Arrays");
		System.exit(-1);
		
	}
	
}
