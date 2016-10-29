package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class LastTriedToEat extends Module {

	private Subject sub;
	private Int0dPort outPort;
	private int lastAte;

	public LastTriedToEat(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Int0dPort(this);
		addOutPort("lastTriedToEatFeeder", outPort);
		
		lastAte = 0;
	}

	@Override
	public void run() {
		if (sub.hasTriedToEat())
			lastAte = sub.getRobot().getLastTriedToEatFeeder();
		outPort.set(lastAte);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
