package edu.usf.ratsim.micronsl;

public abstract class Float2dPort extends Port {

	public Float2dPort(Module owner) {
		super(owner);
	}
	
	public abstract int getNRows();

	public abstract int getNCols();
	
	public abstract float get(int i, int j);

	public float get() {
		return get(0, 0);
	}
	
	public abstract void set(int i, int j, float x);

}
