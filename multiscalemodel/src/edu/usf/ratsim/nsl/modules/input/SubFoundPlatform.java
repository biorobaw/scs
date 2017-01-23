package edu.usf.ratsim.nsl.modules.input;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class SubFoundPlatform extends Module {

	private Bool0dPort outPort;
	private LocalizableRobot robot;

	public SubFoundPlatform(String name, LocalizableRobot lRobot) {
		super(name);
		
		this.robot = lRobot;
		
		outPort = new Bool0dPort(this);
		addOutPort("foundPlatform", outPort);
	}

	@Override
	public void run() {
		outPort.set(robot.hasFoundPlatform());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
