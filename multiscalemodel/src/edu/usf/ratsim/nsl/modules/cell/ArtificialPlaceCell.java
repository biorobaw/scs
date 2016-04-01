package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

public interface ArtificialPlaceCell {

	public float getActivation(Point3f currLocation, float distanceToWall);

	public Point3f getCenter();

	public float getRadius();

}
