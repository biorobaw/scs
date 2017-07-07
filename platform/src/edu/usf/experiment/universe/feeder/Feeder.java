package edu.usf.experiment.universe.feeder;

import com.vividsolutions.jts.geom.Coordinate;

public class Feeder {

	private Coordinate position;
	/**
	 * Determines wheather the feeder can provide food
	 */
	private boolean active;

	private boolean hasFood;

	private boolean isFlashing;
	private boolean enabled;
	private int id;
	private boolean permanent;

	public Feeder(int id, Coordinate position) {
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

	public Coordinate getPosition() {
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

	public void setPosition(Coordinate relFPos) {
		this.position = new Coordinate(relFPos);
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setPermanent(boolean b) {
		this.permanent = b;
	}
}
