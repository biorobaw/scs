package edu.usf.micronsl;

public class FloatMatrixPort extends Float2dPort {

	float[][] data;

	public FloatMatrixPort(Module owner, float[][] data) {
		super(owner);

		if (data.length == 0)
			throw new IllegalArgumentException("Cannot use matrix with 0 rows");

		this.data = data;
	}

	@Override
	public float get(int i, int j) {
		return data[i][j];
	}

	public void set(int i, int j, float x) {
		data[i][j] = x;
	}

	public float[][] getData() {
		return data;
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
}
