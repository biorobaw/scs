package edu.usf.micronsl.port;

import edu.usf.micronsl.module.Module;

/**
 * Ports are used to exchange information between modules.
 * 
 * @author Martin Llofriu
 *
 */
public abstract class Port {

	/**
	 * Owner module of the port. The owner module is used to compute
	 * dependencies based on port connectivity.
	 */
	private Module owner;

	/**
	 * Constructor for the port class
	 * 
	 * @param owner
	 *            Owner module of the port. The owner module is used to compute
	 *            dependencies based on port connectivity.
	 */
	public Port(Module owner) {
		this.owner = owner;
	}

	/**
	 * Gets the owner of the port
	 * 
	 * @return
	 */
	public Module getOwner() {
		return owner;
	}

	/**
	 * Get the size of the port
	 * 
	 * @return The number of data items stored in the port
	 */
	public abstract int getSize();

	/**
	 * Set the ports values to default
	 */
	public abstract void clear();

}
