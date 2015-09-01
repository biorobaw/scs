package edu.usf.ratsim.micronsl;

public abstract class BoolPort extends Port {

	public BoolPort(Module owner) {
		super(owner);
	}
	
	public abstract int getSize();

	public abstract boolean get(int index);


}
