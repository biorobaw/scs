package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * 
 * @author Martin Llofriu
 *
 */
public class SubjectAte extends Module {

	public Bool0dPort outPort;
	private FeederUniverse u;

	public SubjectAte(String name) {
		super(name);

		u = (FeederUniverse)Universe.getUniverse();
		outPort = new Bool0dPort(this);
		addOutPort("subAte", outPort);
	}

	public Bool0dPort getSubAtePort() {
		return outPort;
	}

	@Override
	public void run() {
		
		var eaten = u.hasRobotEaten();
		outPort.set(eaten);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
