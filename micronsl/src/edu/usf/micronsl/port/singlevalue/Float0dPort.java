package edu.usf.micronsl.port.singlevalue;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;

/**
 * This port contains a single item of float data.
 * 
 * @author Martin Llofriu
 *
 */
public class Float0dPort extends Port {

	/**
	 * The value of data
	 */
	private float value;

	/**
	 * Default constructor. The value is initialized to 0.0f.
	 * 
	 * @param owner
	 *            The owner module.
	 */
	public Float0dPort(Module owner) {
		super(owner);
		value = 0.0f;
	}

	/**
	 * A constructor specifing the desired value
	 * 
	 * @param owner
	 *            The owner module
	 * @param value
	 *            The value held by the port
	 */
	public Float0dPort(Module owner, float value) {
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
	public float get() {
		return value;
	}

	/**
	 * Set the value of the port
	 * 
	 * @param value
	 *            The value to store in the port
	 */
	public void set(float value) {
		this.value = value;
	}

	@Override
	public void clear() {
		value = 0f;
	}
}
