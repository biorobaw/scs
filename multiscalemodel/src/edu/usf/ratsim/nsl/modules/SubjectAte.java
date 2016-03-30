package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.Bool1dPort;
import edu.usf.micronsl.Module;


public class SubjectAte extends Module {

	private Subject sub;
	private Bool1dPort outPort;

	public SubjectAte(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Bool1dPort(this);
		addOutPort("subAte", outPort);
	}

	@Override
	public void run() {
		outPort.set(sub.hasEaten());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
