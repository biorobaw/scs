package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;


public class SubjectTriedToEat extends Module {

	private Subject sub;
	private Bool0dPort outPort;

	public SubjectTriedToEat(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Bool0dPort(this);
		addOutPort("subTriedToEat", outPort);
	}

	@Override
	public void run() {
//		System.out.println(sub.hasTriedToEat());
		outPort.set(sub.hasTriedToEat());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
