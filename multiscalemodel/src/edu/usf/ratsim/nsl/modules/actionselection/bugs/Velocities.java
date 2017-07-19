package edu.usf.ratsim.nsl.modules.actionselection.bugs;

public class Velocities {

	public float x;
	public float y;
	public float theta;
	
	// TODO: this should be enforced by the robot
	private static final float MAX_ANGULAR = (float) (Math.PI * 2);
	private static final float MAX_LINEAR = 1f;

	public Velocities(float x, float y, float theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}

	public Velocities() {
		x = 0;
		y = 0;
		theta = 0;
	}

	public void trim() {
		theta = Math.min(MAX_ANGULAR, Math.max(theta, -MAX_ANGULAR));
		x = Math.min(MAX_LINEAR, Math.max(x, -MAX_LINEAR));
		y = Math.min(MAX_LINEAR, Math.max(y, -MAX_LINEAR));
	}

}
