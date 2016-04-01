package edu.usf.ratsim.nsl.modules.cell;

import java.util.Random;

import javax.vecmath.Point3f;

public class WallExponentialArtificialPlaceCell extends
		ExponentialArtificialPlaceCell {

	private boolean wallCell;

	public WallExponentialArtificialPlaceCell(Point3f center, float radius,
			Random r) {
		super(center, radius);

		wallCell = r.nextBoolean();
		// wallCell = true;
	}

	public float getActivation(Point3f currLocation, float distanceToWall) {
		float activation = super.getActivation(currLocation, distanceToWall);
		if (activation != 0) {
			float d = distanceToWall / (getRadius());
			float dAcross = Math.max(0, (d - getCenter().distance(currLocation)
					/ getRadius()));
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				// if ((activation
				// * (1 - 1 / (Math.exp(-10 * (d - .7)) + 1)) * (1 / (Math
				// .exp(-10 * (dAcross - .2)) + 1))) > 1)
				// System.out.println("Activation greater than one");
				return (float) (activation
						* (1 - 1 / (Math.exp(-10 * (d - .7)) + 1)) * (1 / (Math
						.exp(-10 * (dAcross - .2)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				// if (activation * (1 / (Math.exp(-10
				// * (dAcross - .2)) + 1)) > 1)
				// System.out.println("Activation greather than one");
				return (float) (activation * (1 / (Math.exp(-10
						* (dAcross - .2)) + 1)));
			}

		} else
			return 0;
	}
}
