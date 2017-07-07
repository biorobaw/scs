package edu.usf.ratsim.nsl.modules.cell;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Proportional place cells are a computational inexpensive model of place cell. It models the response curve as a cone function.
 * @author Martin Llofriu
 *
 */
public class ProportionalPlaceCell implements PlaceCell {

	private Coordinate center;
	private float radius;
	private float slope;

	public ProportionalPlaceCell(Coordinate center, float radius) {
		this.center = center;
		this.radius = radius;
		
		slope = -1 / radius;
	}

	/**
	 * First, the 0 case is checked. Otherwise, the activation is computed by multiplying using a linear function of the distance
	 * @param currLocation The animat current location
	 * @return The current activation value
	 */
	public float getActivation(Coordinate currLocation) {
		float dist = (float) center.distance(currLocation);
		if (dist >= radius)
			return 0;
		else
			return 1 + dist * slope;
	}

	public Coordinate getPreferredLocation() {
		return center;
	}

	@Override
	public float getPlaceRadius() {
		return radius;
	}

	@Override
	public float getActivation(Coordinate currLocation, float distanceToWall) {
		return getActivation(currLocation);
	}
}
