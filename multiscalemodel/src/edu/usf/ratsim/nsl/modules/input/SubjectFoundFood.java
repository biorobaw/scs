package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

public class SubjectFoundFood extends Module {

	private FeederRobot robot;
	public Bool0dPort outPort;

	public SubjectFoundFood(String name, Robot robot) {
		super(name);
		
		this.robot = (FeederRobot) robot;
		
		outPort = new Bool0dPort(this);
		addOutPort("subFoundFood", outPort);
	}

	@Override
	public void run() {
		outPort.set(robot.hasFoundFood());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
