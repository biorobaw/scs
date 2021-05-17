package com.github.biorobaw.scs.simulation.object;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.simulation.AbstractSimulator;

public abstract class SimulatedObject {

	private long guid = -1; // globally unique identifier (set by the universe)
	AbstractSimulator simulator = Experiment.get().simulator;
	public void set_guid(long guid) {
		this.guid = guid;
	}
	
	public long get_guid() {
		return guid;
	}
	
	// == FUNCTIONS TO ADD AND REMOVE THE OBJECT FROM SIMULATION ===
	
	/**
	 * Adds the object to the simulation at the origin oriented with the identity matrix
	 */
	public void addToSimulation() {
		var z = AbstractSimulator.zVector;
		simulator.addObject(this, new Vector3D(0,0,0),new Rotation(z,0));
	}
	
	/**
	 * Removes the object from the simulation
	 */
	public void removeFromSimulation() {
		simulator.removeObject(this);
	}
	
	// ===== POSITION AND ORIENTATION FUNCTIONS ====================
	
	/**
	 * 
	 * @return Returns the position of the object in the simulator
	 */
	public Vector3D getPosition() {
		return simulator.getObjectPosition(guid);
	}
	
	/**
	 * Sets the position of the object in the simulator
	 * @param position The new position of the object
	 */
	public void setPosition(Vector3D position) {
		simulator.setObjectPosition(guid, position);
	}
	
	/**
	 * 
	 * @return Returns the orientation of the object
	 */
	public Rotation getOrientation() {
		return simulator.getObjectOrientation(guid);
	}
	
	/**
	 * Sets the orientation of the object
	 * @param orientation The new orientation
	 */
	public void setOrientation(Rotation orientation) {
		
	}
	
	/**
	 * Sets the orientation of the object assuming 2D coordinates.
	 * @param angle The new orientation of the object
	 */
	public void setOrientation2D(float angle) {
		var z = AbstractSimulator.zVector;
		simulator.setObjectOrientation(guid, new Rotation(z, angle));
	}
	
	/**
	 * 
	 * @return Returns the orientation of the object assuming 2D coordinates
	 */
	final public float getOrientation2D() {
		return (float)simulator.getObjectOrientation(guid).getAngles(RotationOrder.XYZ)[2];
	}
	
}
