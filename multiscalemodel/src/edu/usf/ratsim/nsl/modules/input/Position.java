package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class Position extends Module {

	private Point3fPort pos;
	private LocalizableRobot robot;

	public Position(String name, LocalizableRobot robot) {
		super(name);
		
		this.robot = robot;
		pos = new Point3fPort(this);
		addOutPort("position", pos);
	}

	@Override
	public void run() {
		pos.set(robot.getPosition()); //note
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
