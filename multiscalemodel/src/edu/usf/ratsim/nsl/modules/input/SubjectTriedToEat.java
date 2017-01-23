package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.subject.SubjectOld;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just tried to eat
 * @author Martin Llofriu
 *
 */
public class SubjectTriedToEat extends Module {

	private SubjectOld sub;
	private Bool0dPort outPort;

	public SubjectTriedToEat(String name, SubjectOld sub) {
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
