package edu.usf.ratsim.nsl.modules.cell;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

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
	private float a;
	private float b;
	private float b2;

	public ExponentialWallConjCell(Coordinate preferredLocation, float preferredDirection, float placeRadius,
									float angleRadius, int preferredIntention, Boolean isWallCell, float a, float b,float b2) {
		super(preferredLocation, preferredDirection, placeRadius, angleRadius, preferredIntention);
		wallCell = isWallCell;
		this.a = a;
		this.b = b;
		this.b2 = b2;
	}
	
	public ExponentialWallConjCell(Coordinate preferredLocation, float preferredDirection, float placeRadius,
									float angleRadius, int preferredIntention, Boolean isWallCell) {
		this(preferredLocation, preferredDirection, placeRadius, angleRadius, preferredIntention,isWallCell,10f,0.2f,0.7f);
		if(isWallCell) {
			b =.7f;
			b2 = .2f;
		}
	}
	

	@Override
	public float getActivation(Coordinate currLocation, float currAngle, int currIntention, float distanceToWall) {
		float activation = super.getActivation(currLocation, currAngle, currIntention, distanceToWall);
		if (activation != 0) {
			float d = distanceToWall / (getPlaceRadius());
			float dAcross = d - (float)getPreferredLocation().distance(currLocation) / getPlaceRadius();
			if(dAcross<=0) return 0;
			
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				return (float) (activation * (1 - 1 / (Math.exp(-a * (d - b)) + 1))* (1 / (Math.exp(-a * (dAcross - b2)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				return (float) (activation * (1 / (Math.exp(-a * (dAcross - b)) + 1)));
			}
			// return activation;
		} else
			return 0;

	}

}
