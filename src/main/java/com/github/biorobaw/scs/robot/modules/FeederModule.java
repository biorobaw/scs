package com.github.biorobaw.scs.robot.modules;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.robot.RobotModule;
import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;
import com.github.biorobaw.scs.utils.XML;

public class FeederModule extends RobotModule {

	// ============ MODULE VARIABLES ===========================
	
	// pointers and constants
	Maze maze; 		 // pointer to the experiment
	float max_feeding_distance_sq;  // controls from how far can the robot eat
	
	// module memory
	Feeder eatingFeeder = null; // feeder trying to eat from
	boolean ate = false;		// whether robot was able to eat
	
	
	// ============ PUBLIC METHODS ============================
	
	/**
	 * Constructor from xml node
	 * @param xml
	 */
	public FeederModule(XML xml) {
		super(xml);
		maze = Experiment.instance.maze;
		max_feeding_distance_sq = xml.hasAttribute("max_feeding_distance") ?
				xml.getFloatAttribute("max_feeding_distance") : 
				0.1f; // default distance is 10cm
		max_feeding_distance_sq *= max_feeding_distance_sq; // store the square value
		
	}
	
	/**
	 * Orders the robot to attempt eating in the next simulation cycle.
	 * If successful an event is generated which can be checked with the function 'ate()'
	 * @param feeder feeder to try eating from
	 */
	public void eat(Feeder feeder) {
		eatingFeeder = feeder;
		simulator.addEventGeneratorSingleUse(this::generateEatEvent);
	}
	
	/**
	 * Returns the list of feeders that the robot can eat from.
	 * @return An array with the feeders the robot cane at from
	 */
	public ArrayList<Feeder> canEatFeeders() {
		var res = new ArrayList<Feeder>(3); // create result array
		var pos = proxy.getPosition(); // get postion
		
		// for each feeder, check whether the robot can eat or not
		for(var f : maze.feeders.values())
			if(canEat(f, pos))
				res.add(f);
		return res;
	}
	
	/**
	 * 
	 * @return returns whether the robot was able to eat in the last simulation cycle
	 */
	public boolean ate() {
		return ate;
	}
	
	
	@Override
	public void clearEvents() {
		eatingFeeder = null;
		ate = false;
	}
	
	// ========== PRIVATE AND PROTECTED METHODS ====================
	/**
	 * Function that defines whether the robot can eat from a
	 * feeder from a given position or not.
	 * @param f	The feeder to eat from
	 * @param pos The postion of the robot
	 * @return	Boolean representing whether the robot can eat
	 */
	protected boolean canEat(Feeder f, Vector3D pos) {
		return f.hasFood && pos.distanceSq(f.pos) < max_feeding_distance_sq;
	}
		
	protected void generateEatEvent() {
		// check if feeder has food
		if(eatingFeeder.hasFood) {
			//check if robot is close enough for eating
			var pos = proxy.getPosition();
			if(pos.distanceSq(eatingFeeder.pos) < max_feeding_distance_sq) {
				eatingFeeder.eat();
			}
		}
	}
	
	
	
}
