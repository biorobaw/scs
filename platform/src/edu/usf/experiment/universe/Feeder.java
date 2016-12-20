package edu.usf.experiment.universe;

import javax.vecmath.Point3f;

public class Feeder {

	private Point3f position;
	/**
	 * Determines wheather the feeder can provide food
	 */
	private boolean active;

	private boolean hasFood;

	private boolean isFlashing;
	private boolean enabled;
	private int id;
	private boolean permanent;

	public Feeder(int id, Point3f position) {
		enabled = false;
		active = false;
		hasFood = false;
		isFlashing = false;
		permanent = false;

		this.position = position;
		this.id = id;
	}
	
	public Feeder(Feeder feeder) {
		this.enabled = feeder.enabled;
		this.active = feeder.active;
		this.hasFood = feeder.hasFood;
		this.isFlashing = feeder.isFlashing;
		this.position = feeder.position;
		this.id = feeder.id;
		this.permanent = feeder.permanent;
	}

	public int getId() {
		return id;
	}

	public boolean isFlashing() {
		return isFlashing;
	}

	public void setFlashing(boolean isFlashing) {
		this.isFlashing = isFlashing;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		// flashing = active;
	}

	public Point3f getPosition() {
		return position;
	}

	public void releaseFood() {
		hasFood = true;
	}

	public void clearFood() {
		hasFood = false;
	}

	public boolean hasFood() {
		return hasFood || permanent;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPosition(Point3f relFPos) {
		this.position = new Point3f(relFPos);
	}

	public void setPermanent(boolean b) {
		this.permanent = b;
	}
}
