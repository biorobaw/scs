package edu.usf.ratsim.nsl.modules.actionselection.bugs;

public class Velocities {

	public float linear;
	public float angular;
	
	// TODO: this should be enforced by the robot
	private static final float MAX_ANGULAR = (float) (Math.PI * 2);
	private static final float MAX_LINEAR = 1f;

	public Velocities(float linear, float angular) {
		this.linear = linear;
		this.angular = angular;
	}

	public Velocities() {
		linear = 0;
		angular = 0;
	}

	public void trim() {
		angular = Math.min(MAX_ANGULAR, Math.max(angular, -MAX_ANGULAR));
		linear = Math.min(MAX_LINEAR, Math.max(linear, -MAX_LINEAR));
	}

}
