package edu.usf.micronsl;

/**
 * Ports are used to exchange information between modules.
 * @author Martin Llofriu
 *
 */
public class Port {

	/**
	 * Owner module of the port. The owner module is used to compute dependencies based on port connectivity.
	 */
	private Module owner;

	/**
	 * Constructor for the port class
	 * @param owner Owner module of the port. The owner module is used to compute dependencies based on port connectivity.
	 */
	public Port(Module owner) {
		this.owner = owner;
	}

	public Module getOwner() {
		return owner;
	}

}
