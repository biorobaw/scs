package com.github.biorobaw.scs.simulation.object.maze_elements;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.simulation.object.SimulatedObject;
import com.github.biorobaw.scs.utils.files.XML;

public class Feeder extends SimulatedObject {

	public Vector3D pos;				// feeder location
	public boolean hasFood = false; // has to be set by a task/script
	public int feeder_id;			// user defined id for the feeder

	public Feeder(int id, float x, float y) {
		this.feeder_id = id;
		pos = new Vector3D(x,y,0);
	}
	
	public Feeder(XML xml) {
		feeder_id = xml.getIntAttribute("id");
		var x = xml.getFloatAttribute("x");
		var y = xml.getFloatAttribute("y");
		pos = new Vector3D(x,y,0);
	}
	
	/**
	 * Defines the behavior of the feeder when a rat eats from it.
	 * Allows to overwrite the default behavior which only sets "hasFood" to false.
	 */
	public void eat() {
		hasFood = false;
	}
	
	
	@Override
	public Vector3D getPosition() {
		return pos;
	}
	
	
}
