package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

import edu.usf.ratsim.experiment.subject.NotImplementedException;

/**
 * Exponential conjunctive cells are exponentially modulated by place and head
 * direction.
 * 
 * @author Martin Llofriu
 * 
 */
public class ExponentialPlaceIntentionCell implements ConjCell {

	/**
	 * The minimum distance to the center for the cell to fire. Below this
	 * value, firing rates are set to 0.
	 */
	private static final double RADIUS_THRS = .2;
	/**
	 * The cell's preferred location
	 */
	private Point3f preferredLocation;
	/**
	 * The dispersion parameter for the gaussian function that modulates the
	 * firing rate according to the current place. This parameter is kept for
	 * performance sake.
	 */
	private float placeDispersion;
	/**
	 * The cell's preferred intention
	 */
	private int preferredIntention;
	/**
	 * The place radius. If the distance from the current position and the
	 * preferred one is greater than this value, the firing is set to 0.
	 * Additionally, the gaussian modulation is tuned to be RADIUS_THRS when the
	 * distance is exactly equal to this radius.
	 */
	private float placeRadius;
	/**
	 * Current bupivacaine modulation
	 */
	private float bupiModulation;
	/**
	 * A parameter kept for peformance sake.
	 */
	private float placeRadiusSquared;

	public ExponentialPlaceIntentionCell(Point3f preferredLocation,
			float placeRadius, int preferredIntention) {
		this.preferredLocation = preferredLocation;
		this.placeRadius = placeRadius;
		this.placeRadiusSquared = (float) Math.pow(placeRadius, 2);
		// min_thrs = e^(-x_min_thrs^2/w) -> ...
		this.placeDispersion = (float) (-Math.pow(placeRadius, 2) / Math
				.log(RADIUS_THRS));
		this.preferredIntention = preferredIntention;
		this.bupiModulation = 1;
	}

	/**
	 * Returns the activation value given all modulation factors. First, common
	 * (0) cases are checked. If the activation is not 0, it is computed using -
	 * The bupivacaine modulation - The place gaussian modulation - The
	 * direction gaussian modulation The intention is not used, because it just
	 * would multiply by 1. These cells make no use of the wall distance factor.
	 */
	public float getActivation(Point3f currLocation, float currAngle,
			int currIntention, float distanceToWall) {
		if (bupiModulation == 0
				|| currIntention != preferredIntention
				|| preferredLocation.distanceSquared(currLocation) > placeRadiusSquared)
			return 0;
		else
			return (float) (bupiModulation
					* Math.exp(-Math.pow(
							preferredLocation.distance(currLocation), 2)
							/ placeDispersion));
	}

	public Point3f getPreferredLocation() {
		return preferredLocation;
	}

	public void setPreferredLocation(Point3f preferredLocation) {
		this.preferredLocation = preferredLocation;
	}

	public int getPreferredIntention() {
		return preferredIntention;
	}

	public void setPreferredIntention(int preferredIntention) {
		this.preferredIntention = preferredIntention;
	}

	public float getPlaceRadius() {
		return placeRadius;
	}

	public void setPlaceRadius(float placeRadius) {
		this.placeRadius = placeRadius;
	}

	public void setBupiModulation(float modulation) {
		this.bupiModulation = modulation;
	}

	@Override
	public float getPreferredDirection() {
		throw new NotImplementedException();
	}

	@Override
	public void setPreferredDirection(float preferredDirection) {
		throw new NotImplementedException();
	}

	@Override
	public float getDirectionRadius() {
		throw new NotImplementedException();
	}

}
