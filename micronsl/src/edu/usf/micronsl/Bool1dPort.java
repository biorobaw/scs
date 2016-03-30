package edu.usf.micronsl;

public class Bool1dPort extends BoolPort {

	private boolean value;

	public Bool1dPort(Module owner) {
		super(owner);
		value = false;
	}

	public Bool1dPort(Module owner, boolean value) {
		super(owner);
		this.value = value;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public boolean get(int index) {
		if (index != 0)
			throw new IllegalArgumentException("Bool1dPort does not have element " + index);

		return value;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

}
