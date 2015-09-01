package edu.usf.ratsim.nsl.modules;

import javax.vecmath.Point3f;

import edu.usf.ratsim.support.GeomUtils;

public class ExponentialConjCell {

	private static final double RADIUS_THRS = .2;
	private Point3f preferredLocation;
	private float preferredDirection;
	private float placeDispersion;
	private float angleDispersion;
	private int preferredIntention;
	private float placeRadius;
	private float angleRadius;
	private float modulation;
	private float placeRadiusSquared;

	public ExponentialConjCell(Point3f preferredLocation,
			float preferredDirection, float placeRadius, float angleRadius,
			int preferredIntention) {
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
		this.preferredIntention = preferredIntention;
		this.modulation = 1;
	}

	public float getActivation(Point3f currLocation, float currAngle,
			int currIntention, float distanceToWall) {
		if (modulation == 0
				|| currIntention != preferredIntention
				|| preferredLocation.distanceSquared(currLocation) > placeRadiusSquared
				|| GeomUtils.angleDistance(currAngle, preferredDirection) > angleRadius)
			return 0;
		else
			return (float) (modulation * Math.exp(-Math.pow(
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

	public float getAngleRadius() {
		return angleRadius;
	}

	public void setAngleRadius(float angleRadius) {
		this.angleRadius = angleRadius;
	}

	public void setBupiModulation(float modulation) {
		this.modulation = modulation;
	}

}
