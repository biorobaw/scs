package edu.usf.ratsim.nsl.modules;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class ClosestFeeder extends Module {

	private Subject sub;
	private Int0dPort outPort;

	public ClosestFeeder(String name, Subject sub) {
		super(name);
		
		this.sub = sub;
		
		outPort = new Int0dPort(this);
		addOutPort("closestFeeder", outPort);
	}

	@Override
	public void run() {
		Feeder closest = sub.getRobot().getClosestFeeder();
		if (closest == null)
			outPort.set(-1);
		else
			outPort.set(closest.getId());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
