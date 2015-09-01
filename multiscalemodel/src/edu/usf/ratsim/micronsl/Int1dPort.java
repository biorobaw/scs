package edu.usf.ratsim.micronsl;

public class Int1dPort extends IntPort {

	int[] data;

	public Int1dPort(Module owner, int[] data) {
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

	public int[] getData() {
		return data;
	}

	public void set(int i, int x) {
		data[i] = x;
	}

}
