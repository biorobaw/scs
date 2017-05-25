package edu.usf.experiment.robot.affordance;

public class ForwardAffordance extends Affordance {

	private float distance;

	public ForwardAffordance(float distance) {
		super();

		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Forward " + distance + " mts.";
	}

}
