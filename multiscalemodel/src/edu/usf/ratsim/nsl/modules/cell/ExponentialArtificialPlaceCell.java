package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

public class ExponentialArtificialPlaceCell implements ArtificialPlaceCell {

	private static final double RADIUS_THRS = .2;
	private Point3f center;
	private float width;
	private float radius;

	public ExponentialArtificialPlaceCell(Point3f center, float radius) {
		this.center = center;
		this.radius = radius;
		// min_thrs = e^(-x_min_thrs^2/w) -> ...
		this.width = (float) (-Math.pow(radius, 2) / Math.log(RADIUS_THRS));
	}

	public float getActivation(Point3f currLocation, float distanceToWall) {
		if (center.distance(currLocation) > radius)
			return 0;
		else
			return (float) Math.exp(-Math.pow(center.distance(currLocation), 2)
					/ width);
	}

	public Point3f getCenter() {
		return center;
	}

	public float getRadius() {
		return radius;
	}
}
