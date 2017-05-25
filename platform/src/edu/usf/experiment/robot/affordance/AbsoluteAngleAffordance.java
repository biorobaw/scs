package edu.usf.experiment.robot.affordance;

public class AbsoluteAngleAffordance extends Affordance {

	private float angle;
	private float distance;

	public AbsoluteAngleAffordance(float angle, float distance) {
		super();

		this.angle = angle;
		this.distance = distance;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Absolute angle " + angle + " rds. distance " + distance + " mts.";
	}



}
