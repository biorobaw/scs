package edu.usf.micronsl.port.singlevalue;

import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.Port;

/**
 * This port contains a single item of boolean data.
 * 
 * @author Martin Llofriu
 *
 */
public class Bool0dPort extends Port {

	/**
	 * The value of data
	 */
	private boolean value;

	/**
	 * Default constructor. The value is initialized to false.
	 * 
	 * @param owner
	 *            The owner module.
	 */
	public Bool0dPort(Module owner) {
		super(owner);
		value = false;
	}

	/**
	 * A constructor specifing the desired value
	 * 
	 * @param owner
	 *            The owner module
	 * @param value
	 *            The value held by the port
	 */
	public Bool0dPort(Module owner, boolean value) {
		super(owner);
		this.value = value;
	}

	@Override
	public int getSize() {
		return 1;
	}

	/**
	 * Get the value held by the port
	 * 
	 * @return the value held by the port
	 */
	public boolean get() {
		return value;
	}

	/**
	 * Set the value of the port
	 * 
	 * @param value
	 *            The value to store in the port
	 */
	public void set(boolean value) {
		this.value = value;
	}

	@Override
	public void clear() {
		value = false;
	}
}
