package edu.usf.micronsl.port.singlevalue;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.PortCopier;

public class Int0dPortCopy extends Int0dPort implements PortCopier {

	/**
	 * The copy of the data
	 */
	private int data;
	
	/**
	 * The source port to be copied
	 */
	private Int0dPort toCopy;
	
	public Int0dPortCopy(Module owner, Int0dPort toCopy) {
		super(owner);
		this.toCopy = toCopy;
	}

	@Override
	public int get() {
		return data;
	}

	@Override
	public void clear() {
		data = 0;
	}

	@Override
	public void copy() {
		data = toCopy.get();
	}

}
