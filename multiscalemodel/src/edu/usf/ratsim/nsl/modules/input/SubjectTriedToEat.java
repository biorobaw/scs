package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;

/**
 * Provides an output port stating whether the subject just tried to eat
 * @author Martin Llofriu
 *
 */
public class SubjectTriedToEat extends Module {

	private Bool0dPort outPort;
	private FeederRobot robot;

	public SubjectTriedToEat(String name, Robot robot) {
		super(name);
		
		this.robot = (FeederRobot) robot;
		
		outPort = new Bool0dPort(this);
		addOutPort("subTriedToEat", outPort);
	}

	@Override
	public void run() {
//		System.out.println(sub.hasTriedToEat());
		outPort.set(robot.hasRobotTriedToEat());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
