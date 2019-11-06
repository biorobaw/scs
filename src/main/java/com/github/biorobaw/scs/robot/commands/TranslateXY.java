package com.github.biorobaw.scs.robot.commands;

/**
 * Action that indicates a robot to move by distances dx and and dy
 * in direction x and y respectively
 * @author bucef
 *
 */
public class TranslateXY  {

	public float dx;
	public float dy;
	
	public TranslateXY(float dx, float dy) {
		this.dx= dx;
		this.dy= dy;
	}
	
}
