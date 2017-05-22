package edu.usf.ratsim.nsl.modules.input;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.FeederUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Provides an output port with the identifier of the closes feeder
 * @author Martin Llofriu
 *
 */
public class ClosestFeeder extends Module {

	private Int0dPort outPort;
	
	private FeederRobot fr;

	public ClosestFeeder(String name, Robot robot) {
		super(name);
		
		outPort = new Int0dPort(this);
		addOutPort("closestFeeder", outPort);
		
		fr = (FeederRobot) robot;
	}

	@Override
	public void run() {
		Feeder closest = FeederUtils.getClosestFeeder(fr.getVisibleFeeders());
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
