package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class HeadDirection extends Module {

	private Float0dPort hd;
	private LocalizableRobot robot;

	public HeadDirection(String name, LocalizableRobot robot) {
		super(name);
		
		this.robot = robot;
		hd = new Float0dPort(this);
		addOutPort("orientation", hd);
	}

	@Override
	public void run() {
		hd.set(robot.getOrientationAngle());//note
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
