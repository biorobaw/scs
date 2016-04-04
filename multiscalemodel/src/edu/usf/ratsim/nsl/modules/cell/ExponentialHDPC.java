package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

import edu.usf.experiment.utils.GeomUtils;

/**
 * Exponential head direction place cells are exponentially modulated by place and head direction.  
 * @author Martin Llofriu
 *
 */
public class ExponentialHDPC implements ConjCell {

	/**
	 * The minimum distance to the center for the cell to fire. Below this value, firing rates are set to 0.
	 */
	private static final double RADIUS_THRS = .2;
	/**
	 * The cell's preferred location
	 */
	private Point3f preferredLocation;
	/**
	 * The cell's preferred direction
	 */
	private float preferredDirection;
	/**
	 * The dispersion parameter for the gaussian function that modulates the firing rate according to the current place.
	 * This parameter is kept for performance sake.
	 */
	private float placeDispersion;
	/**
	 * The dispersion parameter for the gaussian function that modulates the firing rate according to the current orientation
	 * This parameter is kept for performance sake.
	 */
	private float angleDispersion;
	/**
	 * The place radius. If the distance from the current position and the preferred one is greater than this value, the firing is set to 0.
	 * Additionally, the gaussian modulation is tuned to be RADIUS_THRS when the distance is exactly equal to this radius.
	 */
	private float placeRadius;
	/**
	 * The angular radius. If the difference between current and preferred heading is greater thant this value, the firing is set to 0.
	 */
	private float angleRadius;
	/**
	 * Current bupivacaine modulation
	 */
	private float bupiModulation;
	/**
	 * A parameter kept for peformance sake.
	 */
	private float placeRadiusSquared;

	public ExponentialHDPC(Point3f preferredLocation,
			float preferredDirection, float placeRadius, float angleRadius) {
		this.preferredLocation = preferredLocation;
		this.preferredDirection = preferredDirection;
		this.placeRadius = placeRadius;
		this.angleRadius = angleRadius;
		this.placeRadiusSquared = (float) Math.pow(placeRadius, 2);
		// min_thrs = e^(-x_min_thrs^2/w) -> ...
		this.placeDispersion = (float) (-Math.pow(placeRadius, 2) / Math
				.log(RADIUS_THRS));
		this.angleDispersion = (float) (-Math.pow(angleRadius, 2) / Math
				.log(RADIUS_THRS));
		this.bupiModulation = 1;
	}

	/**
	 * Returns the activation value given all modulation factors.
	 * First, common (0) cases are checked. If the activation is not 0, it is computed using
	 *  - The bupivacaine modulation
	 *  - The place gaussian modulation
	 *  - The direction gaussian modulation
	 * These cells make no use of the wall distance factor.
	 */
	public float getActivation(Point3f currLocation, float currAngle,
			int currIntention, float distanceToWall) {
		if (bupiModulation == 0
				|| preferredLocation.distanceSquared(currLocation) > placeRadiusSquared
				|| GeomUtils.angleDistance(currAngle, preferredDirection) > angleRadius)
			return 0;
		else
			return (float) (bupiModulation * Math.exp(-Math.pow(
					preferredLocation.distance(currLocation), 2)
					/ placeDispersion) * Math.exp(-Math.pow(
					GeomUtils.angleDistance(currAngle, preferredDirection), 2)
					/ angleDispersion));
	}

	public Point3f getPreferredLocation() {
		return preferredLocation;
	}

	public void setPreferredLocation(Point3f preferredLocation) {
		this.preferredLocation = preferredLocation;
	}

	public float getPreferredDirection() {
		return preferredDirection;
	}

	public void setPreferredDirection(float preferredDirection) {
		this.preferredDirection = preferredDirection;
	}

	public float getPlaceRadius() {
		return placeRadius;
	}

	public void setPlaceRadius(float placeRadius) {
		this.placeRadius = placeRadius;
	}

	public float getDirectionRadius() {
		return angleRadius;
	}

	public void setAngleRadius(float angleRadius) {
		this.angleRadius = angleRadius;
	}

	public void setBupiModulation(float modulation) {
		this.bupiModulation = modulation;
	}

	@Override
	public int getPreferredIntention() {
		return 0;
	}

}
