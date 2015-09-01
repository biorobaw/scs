package edu.usf.experiment.log;

public class Pose {
	public float x, y;
	public boolean randomAction;
	public boolean triedToEat;
	public boolean ate;

	public Pose(float x, float y, boolean randomAction, boolean triedToEat, boolean ate) {
		super();
		this.x = x;
		this.y = y;
		this.randomAction = randomAction;
		this.triedToEat = triedToEat;
		this.ate = ate;
	}
}
