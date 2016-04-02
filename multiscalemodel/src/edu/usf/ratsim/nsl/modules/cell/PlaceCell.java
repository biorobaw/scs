package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

/**
 * The PlaceCell interface represents a firing unit that has a preferred firing place and can also be modulated by the presence of a wall.
 * @author Martin Llofriu
 *
 */
public interface PlaceCell {
	
	/**
	 * The activation value given the modulating parameters.
	 * @param currLocation The current location of the animat
	 * @param distToWall The distance to the closest wall
	 * @return The activation value corresponding to the firing rate of the cell
	 */
	public float getActivation(Point3f currLocation, float distToWall);

	/**
	 * Get the preferred location of firing
	 * @return The preferred point where the fire is at its peak
	 */
	public Point3f getPreferredLocation();

	/**
	 * The radius of the firing field. Outside this radius, the firing is 0.
	 * @return The radius of the firing field.
	 */
	public float getPlaceRadius();

}
