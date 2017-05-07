package edu.usf.ratsim.nsl.modules.cell;

import java.util.Random;

import javax.vecmath.Point3f;

/**
 * Exponential wall cells add a factor of modulation to their
 * superclass ExponentialCell. These cells can be one of two different
 * types: - Possitively modulated by the presence of walls (wall cells) -
 * Negatively modulated by the presence of walls (non wall cells) The type of
 * cell is decided at build time.
 * 
 * Wall cells are modulated by two sigmoid functions. The first is an inverted
 * sigmoid function (1 - sigmoid) of the distance to the wall. Hence, closer to
 * the wall means a greater activation. The second is a function of the
 * difference between the distance to the wall and the distance to the preferred
 * location. This prevents cells from firing on both sides of a wall, firing
 * only on the side where their preferred place is.
 * 
 * Non wall cells are only modulated by the second function and are inhibited by
 * the presence of walls, firing only on one side of them.
 * 
 * @author Martin Llofriu
 *
 */
public class WallExponentialPlaceCell extends
		ExponentialPlaceCell {

	private boolean wallCell;

	public WallExponentialPlaceCell(Point3f center, float radius,
			Random r) {
		super(center, radius);

		wallCell = r.nextBoolean();
	}

	public float getActivation(Point3f currLocation, float distanceToWall) {
		float activation = super.getActivation(currLocation);
		if (activation != 0) {
			float d = distanceToWall / (getPlaceRadius());
			float dAcross = Math.max(0, (d - getPreferredLocation().distance(currLocation)
					/ getPlaceRadius()));
			
			// No cell firing across a wall
			if (dAcross == 0)
				return 0;
			
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				return (float) (activation
						* (1 - 1 / (Math.exp(-10 * (d - .7)) + 1)) * (1 / (Math
						.exp(-10 * (dAcross - .2)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				return (float) (activation * (1 / (Math.exp(-10
						* (dAcross - .2)) + 1)));
			}

		} else
			return 0;
	}
}
