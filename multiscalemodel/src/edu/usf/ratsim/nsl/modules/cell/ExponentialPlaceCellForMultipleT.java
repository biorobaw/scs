package edu.usf.ratsim.nsl.modules.cell;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Gaussian Place cells
 * 
 */
public class ExponentialPlaceCellForMultipleT implements PlaceCell {

	
	/**
	 * The cell's preferred location
	 */
	private Coordinate center;
	/**
	 * standard deviation
	 */
	private float stdDev;
	/**
	 * variance
	 */
	private float var2;
	/**
	 * Radius of activation
	 */
	private float normalizationValue;
	
	private float activationRadius;


	public ExponentialPlaceCellForMultipleT(Coordinate center, float stdDev) {
		this.center = center;
		this.stdDev = stdDev;
		// min_thrs = e^(-x_min_thrs^2/w) -> ...
		this.var2 = stdDev*stdDev*2;
		this.activationRadius = 2.3f*stdDev;
		this.normalizationValue = (float)Math.sqrt(2*Math.PI*stdDev);
	}

	/**
	 * Outside the place field radius, the activation is 0. Inside the place
	 * field, the response curve corresponds to a gaussian function of the
	 * distance
	 * 
	 * @param currLocation
	 * @return
	 */
	public float getActivation(Coordinate currLocation) {
		if (center.distance(currLocation) > activationRadius)
			return 0;
		else
			return (float) Math.exp(-Math.pow(center.distance(currLocation), 2) / var2);
	}

	public Coordinate getPreferredLocation() {
		return center;
	}

	public float getPlaceRadius() {
		return activationRadius;
	}

	@Override
	public float getActivation(Coordinate currLocation, float distanceToWall) {
		return getActivation(currLocation);
	}
}
