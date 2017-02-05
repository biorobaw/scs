package edu.usf.micronsl.port.singlevalue;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.PortCopier;

/**
 * This port contains a single item of integer data.
 * 
 * @author Martin Llofriu
 *
 */
public class Int0dPortCopy extends Int0dPort implements PortCopier{

	/**
	 * The value of data
	 */
	private int value;
	private Int0dPort toCopy;

	/**
	 * A constructor specifying the source port to be copied
	 * 
	 * @param owner
	 *            The owner module.
	 */
	public Int0dPortCopy(Module owner,Int0dPort toCopy) {
		super(owner);
		value = toCopy.get();
		this.toCopy = toCopy;
	}

	/**
	 * Get the value held by the port
	 * 
	 * @return the value held by the port
	 */
	public int get() {
		return value;
	}

	/**
	 * Set the value of the port
	 * 
	 * @param value
	 *            The value to store in the port
	 */
	public void set(int value) {
		this.value = value;
	}

	@Override
	public void clear() {
		value = 0;
	}

	@Override
	public void copy() {
		value = toCopy.get();
	}
}
