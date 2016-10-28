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
public class LastAteFeeder extends Module {

	private Subject sub;
	private Int0dPort outPort;
	private int lastAte;

	public LastAteFeeder(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Int0dPort(this);
		addOutPort("lastAteFeeder", outPort);
		
		lastAte = -1;
	}

	@Override
	public void run() {
		if (sub.hasEaten())
			lastAte = sub.getRobot().getLastAteFeeder();
		outPort.set(lastAte);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
