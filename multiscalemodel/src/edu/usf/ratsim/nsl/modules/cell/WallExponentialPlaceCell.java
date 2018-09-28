package edu.usf.ratsim.nsl.modules.cell;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

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
	private float a;
	private float b;
	private float b2;

	
	public WallExponentialPlaceCell(Coordinate center, float radius,boolean isWallCell,
			float a, float b,float b2) {
		super(center, radius);
		wallCell = isWallCell;
		this.a = a;
		this.b = b;
		this.b2 = b2;
		
	}
	
	public WallExponentialPlaceCell(Coordinate center, float radius,
			boolean isWallCell) {
		this(center, radius,isWallCell,10f,0.2f,0.7f);
		if(isWallCell) {
			b =.7f;
			b2 = .2f;
		}

	}
	
	public WallExponentialPlaceCell(Coordinate center, float radius,
			Random r) {
		this(center, radius,r.nextBoolean());

	}

	public float getActivation(Coordinate currLocation, float distanceToWall) {
		float activation = super.getActivation(currLocation);
		if (activation != 0) {
			float d = distanceToWall / (getPlaceRadius());
			float dAcross = d - (float)getPreferredLocation().distance(currLocation)/ getPlaceRadius();
			
			// No cell firing across a wall
			if (dAcross <= 0) return 0;
			
			if (wallCell) {
				// If it is a wall cell, it activates more near walls but no
				// across also
				//return (float) (activation* (1 - 1 / (Math.exp(-a * (d - .7)) + 1)) * (1 / (Math.exp(-a * (dAcross - .2)) + 1)));
				return (float) (activation* (1 - 1 / (Math.exp(-a * (d - b)) + 1)) * (1 / (Math.exp(-a * (dAcross - b2)) + 1)));
			} else {
				// If it is not a wall cell, it should activate less near walls
				return (float) (activation *                         (1 / (Math.exp(-a * (dAcross - b)) + 1)));
			}

		} else
			return 0;
	}
}
