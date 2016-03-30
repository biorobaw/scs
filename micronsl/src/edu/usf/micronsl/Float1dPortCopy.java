package edu.usf.micronsl;

public class Float1dPortCopy extends Float1dPort {

	private float[] data = null;
	private Float1dPort toCopy;

	public Float1dPortCopy(Module owner, Float1dPort toCopy) {
		super(owner);

		data = new float[toCopy.getSize()];

		this.toCopy = toCopy;
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public float get(int index) {
		return data[index];
	}

	public void copy() {
		// for (int i = 0; i < toCopy.getSize(); i++)
		// data[i] = toCopy.get(i);
		toCopy.getData(data);
	}

	@Override
	public float[] getData() {
		return data;
	}

	@Override
	public void getData(float[] buf) {
		System.arraycopy(data, 0, buf, 0, data.length);
	}

}
