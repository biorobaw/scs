package edu.usf.ratsim.nsl.modules;

import java.util.Random;

import javax.vecmath.Point3f;

public class ExponentialWallConjCell extends ExponentialConjCell {

	private boolean wallCell;
	private float wallInhibition;

	public ExponentialWallConjCell(Point3f preferredLocation,
			float preferredDirection, float placeRadius, float angleRadius,
			int preferredIntention, float wallInhibition, Random r) {
		super(preferredLocation, preferredDirection, placeRadius, angleRadius,
				preferredIntention);
		wallCell = r.nextBoolean();
		this.wallInhibition = wallInhibition;
	}

	@Override
	public float getActivation(Point3f currLocation, float currAngle,
			int currIntention, float distanceToWall) {
		float activation =  super.getActivation(currLocation, currAngle, currIntention,
				distanceToWall);
		if (activation != 0) {
			float d = distanceToWall / (getPlaceRadius());
			float dAcross = Math.max(0, (d - getPreferredLocation().distance(currLocation)
					/ getPlaceRadius()));
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				return (float) (activation
						* (1 - 1 / (Math.exp(-10 * (d - wallInhibition)) + 1)) * (1 / (Math
						.exp(-10 * (dAcross - .01)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				return (float) (activation * (1 / (Math.exp(-10
						* (dAcross - wallInhibition)) + 1)));
			}
//			return activation;
		} else
			return 0;
		
		
	}
	
	

}
