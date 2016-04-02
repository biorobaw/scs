package edu.usf.ratsim.nsl.modules.cell;

import javax.vecmath.Point3f;

/**
 * Conjunctive cells are units that are modulated by more than one parameter. In
 * this implementation, conjunctive cells are modulated by place, head
 * direction, current intention and the distance to walls. The intention
 * represents the current subtask of a task, following the multiple maps
 * hypothesis. The cell will only fire if the preferred intention is equal to
 * the current one.
 * 
 * @author Martin Llofriu
 *
 */
public interface ConjCell {

	/**
	 * The activation value given the modulating parameters.
	 * 
	 * @param currLocation
	 *            The current location of the animat
	 * @param currAngle
	 *            The current heading angle of the animat
	 * @param currIntention
	 *            The current intention of the animal
	 * @param distToWall
	 *            The distance to the closest wall
	 * @return The activation value corresponding to the firing rate of the cell
	 */
	public float getActivation(Point3f currLocation, float currAngle, int currIntention, float distanceToWall);

	/**
	 * Get the preferred location of firing
	 * 
	 * @return The preferred point where the fire is at its peak
	 */
	public Point3f getPreferredLocation();

	/**
	 * Set the preferred point of firing
	 * 
	 * @param prefLocation
	 *            The preferred point of firing.
	 */
	public void setPreferredLocation(Point3f prefLocation);

	/**
	 * The radius of the firing field. Outside this radius, the firing is 0.
	 * 
	 * @return The radius of the firing field.
	 */
	public float getPlaceRadius();

	/**
	 * The the preferred head direction, where the head direction modulation is
	 * at its peak
	 * 
	 * @return
	 */
	public float getPreferredDirection();

	/**
	 * Set the preferred direction for head direction modulation
	 */
	public void setPreferredDirection(float preferredDirection);

	/**
	 * Get the radius of head direction modulation. Outside this radius, the
	 * cell's activation is 0
	 * 
	 * @return The head direction modulation radius
	 */
	public float getDirectionRadius();

	/**
	 * Get the preferred intention of the cell. The cell will only fire if the
	 * current intention is equal to the preferred one
	 * 
	 * @return The preferred intention.
	 */
	public int getPreferredIntention();

	/**
	 * This method sets a bupivacaine modulation used to simulate HPC temporal
	 * inactivation
	 * 
	 * @param f
	 *            A factor corresponding to the modulation. 1 means no
	 *            modulation, 0 means total shutdown.
	 */
	public void setBupiModulation(float f);

}
