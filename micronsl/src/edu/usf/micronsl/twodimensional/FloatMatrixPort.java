package edu.usf.micronsl.twodimensional;

import edu.usf.micronsl.Module;

/**
 * A two dimensional port using a two dimensional array to hold the data
 * 
 * @author Martin Llofriu
 *
 */
public class FloatMatrixPort extends Float2dPort {

	/**
	 * The array to hold the values
	 */
	float[][] data;

	/**
	 * Create the port using the data array as the structure to hold the data
	 * 
	 * @param owner
	 *            The owner module
	 * @param data
	 *            A 2d array that will be used to hold the data. Notice that
	 *            this array is passed by reference.
	 */
	public FloatMatrixPort(Module owner, float[][] data) {
		super(owner);

		if (data.length == 0 || data[0].length == 0)
			throw new IllegalArgumentException("Cannot use matrix with 0 rows");

		this.data = data;
	}

	@Override
	public float get(int i, int j) {
		return data[i][j];
	}

	@Override
	public void set(int i, int j, float x) {
		data[i][j] = x;
	}

	@Override
	public int getNRows() {
		return data.length;
	}

	@Override
	public int getNCols() {
		if (data.length == 0)
			return 0;
		else
			return data[0].length;
	}

	@Override
	public void clear() {
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = 0f;
	}

	@Override
	public float[][] getData() {
		return data;
	}

	@Override
	public void getData(float[][] data) {
		if (data.length != this.data.length || (data[0].length != this.data[0].length))
			throw new IllegalArgumentException("The data array should be of size getNRows() by getNCols()");

		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = this.data[i][j];
	}
}
