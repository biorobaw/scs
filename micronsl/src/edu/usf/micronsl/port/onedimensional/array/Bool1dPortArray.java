package edu.usf.micronsl.port.onedimensional.array;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Bool1dPort;

/**
 * A port that holds a 1 dimensional set of booleans using a native array.
 * 
 * @author Martin Llofriu
 *
 */
public class Bool1dPortArray extends Bool1dPort {

	/**
	 * The array to hold the data
	 */
	boolean[] data;

	/**
	 * Constructor that takes the data as an argument. Take into account that
	 * arrays are passed by reference (i.e. it is not copied)
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            The data array to use as storage.
	 */
	public Bool1dPortArray(Module owner, boolean[] data) {
		super(owner);

		this.data = data;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public boolean get(int index) {
		return data[index];
	}

	/**
	 * Set the value of one element
	 * 
	 * @param i
	 *            The index
	 * @param x
	 *            The value to be set
	 */
	public void set(int i, boolean x) {
		data[i] = x;
	}

	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			data[i] = false;
	}

	/**
	 * Get access to the internal data storage
	 * 
	 * @return
	 */
	public boolean[] getData() {
		return data;
	}

	@Override
	public void getData(boolean[] data) {
		System.arraycopy(this.data, 0, data, 0, getSize());
	}

}
