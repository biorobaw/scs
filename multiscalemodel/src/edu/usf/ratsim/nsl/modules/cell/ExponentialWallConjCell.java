package edu.usf.ratsim.nsl.modules.cell;

import java.util.Random;

import javax.vecmath.Point3f;

/**
 * Exponential wall conjunctive cells add a factor of modulation to their
 * superclass ExponentialConjCell. These cells can be one of two different
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
public class ExponentialWallConjCell extends ExponentialConjCell {

	private boolean wallCell;
	private float b;
	private float a;

	public ExponentialWallConjCell(Point3f preferredLocation, float preferredDirection, float placeRadius,
			float angleRadius, int preferredIntention, Random r, float a, float b) {
		super(preferredLocation, preferredDirection, placeRadius, angleRadius, preferredIntention);
		wallCell = r.nextBoolean();
		this.a = a;
		this.b = b;
	}

	@Override
	public float getActivation(Point3f currLocation, float currAngle, int currIntention, float distanceToWall) {
		float activation = super.getActivation(currLocation, currAngle, currIntention, distanceToWall);
		if (activation != 0) {
			float d = distanceToWall / (getPlaceRadius());
			float dAcross = Math.max(0, (d - getPreferredLocation().distance(currLocation) / getPlaceRadius()));
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				return (float) (activation * (1 - 1 / (Math.exp(-a * (d - b)) + 1))
						* (1 / (Math.exp(-a * (dAcross - .01)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				return (float) (activation * (1 / (Math.exp(-a * (dAcross - b)) + 1)));
			}
			// return activation;
		} else
			return 0;

	}

}
