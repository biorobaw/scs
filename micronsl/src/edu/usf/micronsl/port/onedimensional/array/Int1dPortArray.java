package edu.usf.micronsl.port.onedimensional.array;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.Int1dPort;

/**
 * A port that holds a 1 dimensional set of integers using a native array.
 * 
 * @author Martin Llofriu
 *
 */
public class Int1dPortArray extends Int1dPort {

	/**
	 * The array to hold the data
	 */
	int[] data;

	/**
	 * Constructor that takes the data as an argument. Take into account that
	 * arrays are passed by reference (i.e. it is not copied)
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            The data array to use as storage.
	 */
	public Int1dPortArray(Module owner, int[] data) {
		super(owner);

		this.data = data;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public int get(int index) {
		return data[index];
	}

	@Override
	public void set(int i, int x) {
		data[i] = x;
	}

	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
	}

	/**
	 * Get access to the internal data storage. The array is returned as a
	 * reference, so it is not of exclusive access.
	 * 
	 * @return
	 */
	@Override
	public int[] getData() {
		return data;
	}

	@Override
	public void getData(int[] data) {
		System.arraycopy(this.data, 0, data, 0, getSize());
	}

}
