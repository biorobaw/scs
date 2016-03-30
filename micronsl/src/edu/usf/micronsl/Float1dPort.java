package edu.usf.micronsl;

public abstract class Float1dPort extends Port {

	public Float1dPort(Module owner) {
		super(owner);
	}

	public abstract int getSize();

	public abstract float get(int index);

	public float get() {
		return get(0);
	}

	public abstract float[] getData();

	public abstract void getData(float[] data);
}
