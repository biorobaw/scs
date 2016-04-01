package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

public class ProportionalArtificialPlaceCell implements ArtificialPlaceCell {

	private Point3f center;

	public ProportionalArtificialPlaceCell(Point3f center) {
		this.center = center;
	}

	public float getActivation(Point3f currLocation, float distanceToWall) {
		return 1 / center.distance(currLocation);
	}

	public Point3f getCenter() {
		return center;
	}

	@Override
	public float getRadius() {
		return 0;
	}
}
