package edu.usf.ratsim.micronsl;

public class Port {

	private Module owner;

	public Port(Module owner) {
		this.owner = owner;
	}

	public Module getOwner() {
		return owner;
	}

}
