package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class SubFoundPlatform extends Module {

	private Bool0dPort outPort;
	private PlatformRobot pr;

	public SubFoundPlatform(String name, PlatformRobot robot) {
		super(name);
		
		this.pr = robot;
		
		outPort = new Bool0dPort(this);
		addOutPort("foundPlatform", outPort);
	}

	@Override
	public void run() {
		outPort.set(pr.hasFoundPlatform());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
