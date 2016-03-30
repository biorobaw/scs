package edu.usf.micronsl;

public abstract class IntPort extends Port {

	public IntPort(Module owner) {
		super(owner);
	}

	public abstract int getSize();

	public abstract int get(int index);

	public int get() {
		return get(0);
	}

}
