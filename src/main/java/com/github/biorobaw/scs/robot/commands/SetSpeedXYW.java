package com.github.biorobaw.scs.robot.commands;

/**
 * Action that sets the linear speeds to vx and vy, and the angular speed to w
 * @author bucef
 *
 */
public class SetSpeedXYW  {

	public float vx;
	public float vy;
	public float w;
	
	public SetSpeedXYW(float vx, float vy, float w) {
		this.vx= vx;
		this.vy= vy;
		this.w= w;
	}
	
}
