package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

/**
 * Gaussian Place cells
 * 
 */
public class ExponentialPlaceCellForMultipleT implements PlaceCell {

	
	/**
	 * The cell's preferred location
	 */
	private Point3f center;
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


	public ExponentialPlaceCellForMultipleT(Point3f center, float stdDev) {
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
	public float getActivation(Point3f currLocation) {
		if (center.distance(currLocation) > activationRadius)
			return 0;
		else
			return (float) Math.exp(-Math.pow(center.distance(currLocation), 2) / var2);
	}

	public Point3f getPreferredLocation() {
		return center;
	}

	public float getPlaceRadius() {
		return activationRadius;
	}

	@Override
	public float getActivation(Point3f currLocation, float distanceToWall) {
		return getActivation(currLocation);
	}
}
