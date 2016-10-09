package edu.usf.micronsl.port.onedimensional.array;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

/**
 * A port that holds a 1 dimensional set of floats using a native array.
 * 
 * @author Martin Llofriu
 *
 */
public class Float1dPortArray extends Float1dPort {

	/**
	 * The array to hold the data
	 */
	float[] data;

	/**
	 * Constructor that takes the data as an argument. Take into account that
	 * arrays are passed by reference (i.e. it is not copied)
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            The data array to use as storage.
	 */
	public Float1dPortArray(Module owner, float[] data) {
		super(owner);

		this.data = data;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public float get(int index) {
		return data[index];
	}

	@Override
	public void set(int i, float x) {
		data[i] = x;
	}

	public void set(float[] data) {
		this.data = data;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			data[i] = 0f;
	}

	/**
	 * Get access to the internal data storage. The array is returned as a
	 * reference, so it is not of exclusive access.
	 * 
	 * @return
	 */
	@Override
	public float[] getData() {
		return data;
	}

	@Override
	public void getData(float[] data) {
		System.arraycopy(this.data, 0, data, 0, getSize());
	}

}
