package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

/**
 * Exponential place cells are a canonical model of place cells. The response
 * curve of activation (firing rate) is modeled as a gaussian function of the
 * distance to the preferred location.
 * For performance sake, an absolute radius is fixed, outside of where the activation if 0.
 * 
 * @author Martin Llofriu
 *
 */
public class ExponentialPlaceCellNoThresholding implements PlaceCell, ConjCell {

	/**
	 * The minimum distance to the center for the cell to fire. Below this
	 * value, firing rates are set to 0.
	 */
	private static final double RADIUS_THRS = .2;
	/**
	 * The cell's preferred location
	 */
	private Point3f center;
	/**
	 * The dispersion parameter for the gaussian function that modulates the
	 * firing rate according to the current place. This parameter is kept for
	 * performance sake.
	 */
	private float width;
	/**
	 * The place radius. If the distance from the current position and the
	 * preferred one is greater than this value, the firing is set to 0.
	 * Additionally, the gaussian modulation is tuned to be RADIUS_THRS when the
	 * distance is exactly equal to this radius.
	 */
	private float radius;

	public ExponentialPlaceCellNoThresholding(Point3f center, float radius) {
		this.center = center;
		this.radius = radius;
		// min_thrs = e^(-x_min_thrs^2/w) -> ...
		this.width = (float) (-Math.pow(radius, 2) / Math.log(RADIUS_THRS));
	}

	public Point3f getCenter()
	{
		return center;
	}
	
	public float getWidth()
	{
		return width;
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
		return (float) Math.exp(-Math.pow(center.distance(currLocation), 2) / width);
	}

	public Point3f getPreferredLocation() {
		return center;
	}

	public float getPlaceRadius() {
		return radius;
	}

	@Override
	public float getActivation(Point3f currLocation, float distanceToWall) {
		return getActivation(currLocation);
	}

	@Override
	public float getActivation(Point3f currLocation, float currAngle, int currIntention, float distanceToWall) {
		return getActivation(currLocation);
	}

	@Override
	public void setPreferredLocation(Point3f prefLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getPreferredDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPreferredDirection(float preferredDirection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getDirectionRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPreferredIntention() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBupiModulation(float f) {
		// TODO Auto-generated method stub
		
	}
}
