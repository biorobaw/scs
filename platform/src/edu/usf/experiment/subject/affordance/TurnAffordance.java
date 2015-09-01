package edu.usf.experiment.subject.affordance;

public class TurnAffordance extends Affordance {

	private float angle;
	private float distance;

	public TurnAffordance(float angle, float distance) {
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

	@Override
	public String toString() {
		return "Rotate " + angle + " rads";
	}

	@Override
	public int getIndex() {
		// Positive angles (left) are 0 affordance
		if (angle > 0)
			return 0;
		else
			return 2;
	}
}
