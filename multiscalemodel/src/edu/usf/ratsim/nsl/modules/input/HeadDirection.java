package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

/**
 * Provides an output port stating whether the subject just ate
 * @author Martin Llofriu
 *
 */
public class HeadDirection extends Module {

	public Float0dPort hd;
	private LocalizableRobot robot;

	public HeadDirection(String name, LocalizableRobot robot) {
		super(name);
		
		this.robot = robot;
		hd = new Float0dPort(this);
		addOutPort("hd", hd);
	}

	@Override
	public void run() {
		//System.out.println("HD");
		hd.set(robot.getOrientationAngle());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}
