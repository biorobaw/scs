package com.github.biorobaw.scs.simulation.scs_simulator;

import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.utils.XML;

/**
 * Class implements the proxy for the SCSRobot
 * List of supported commands:
 * 		SetSpeedsVW
 * 		SetSpeedsXYW
 * 		TranslateXY
 * 		RotateT
 * 		StepD
 * @author bucef
 *
 */
public class SCSRobotProxy extends RobotProxy{
	
	protected SCSRobot robot;
	
	public SCSRobotProxy(XML xml) {
		
	}
	
	public void send_command(Object command) {
		robot.receive_command(command);
	}
	
	
	
}
