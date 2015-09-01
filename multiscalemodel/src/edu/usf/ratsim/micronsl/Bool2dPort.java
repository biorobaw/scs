package edu.usf.ratsim.micronsl;

public class Bool2dPort extends BoolPort {

	private boolean[] data;

	public Bool2dPort(Module owner, boolean[] data) {
		super(owner);
		this.data = data;
	}
	
	public Bool2dPort(Module owner, int size) {
		super(owner);
		this.data = new boolean[size];
	}

	@Override
	public int getSize() {
		return data.length;
	}

	@Override
	public boolean get(int index) {
		return data[index];
	}
	
	public boolean[] getData(){
		return data;
	}

}
