package com.github.biorobaw.scs.robot.commands;

/**
 * Action that sets the speeds of a differential drive robot to 
 * linear speed v, and angular speed w
 * @author bucef
 *
 */
public class SetSpeedVW {
	public float v;
	public float w;
	
	public SetSpeedVW(float v, float w) {
		this.v= v;
		this.w= w;
	}
}
