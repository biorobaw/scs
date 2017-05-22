package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class SubjectAte extends Module {

	private Bool0dPort outPort;
	private FeederRobot robot;

	public SubjectAte(String name, Robot robot) {
		super(name);
		
		this.robot = (FeederRobot) robot;
		
		outPort = new Bool0dPort(this);
		addOutPort("subAte", outPort);
	}

	@Override
	public void run() {
		outPort.set(robot.hasRobotEaten());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
