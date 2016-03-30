package edu.usf.micronsl;

public class Int0dPort extends IntPort {

	private int value;

	public Int0dPort(Module owner) {
		super(owner);
		value = 0;
	}

	public Int0dPort(Module owner, int value) {
		super(owner);
		this.value = value;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public int get(int index) {
		if (index != 0)
			throw new IllegalArgumentException("Bool1dPort does not have element " + index);

		return value;
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
	}
}
