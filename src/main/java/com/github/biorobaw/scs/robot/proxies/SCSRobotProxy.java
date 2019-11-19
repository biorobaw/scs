package com.github.biorobaw.scs.robot.proxies;

import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.simulation.scs_simulator.SCSRobot;
import com.github.biorobaw.scs.utils.files.XML;

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
	
	public void setRobot(SCSRobot robot) {
		this.robot = robot;
	}
	
	
	
}
