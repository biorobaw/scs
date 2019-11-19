package com.github.biorobaw.scs.robot.modules;

import java.util.LinkedList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.robot.RobotModule;
import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;
import com.github.biorobaw.scs.utils.files.XML;

public class FeederModule extends RobotModule {

	// ============ MODULE VARIABLES ===========================
	
	// pointers and constants
	Maze maze; 		 // pointer to the experiment
	float max_feeding_distance_sq;  // controls from how far can the robot eat
	
	// module memory
	Feeder eatingFeeder = null; // feeder trying to eat from
	boolean ate = false;		// whether robot was able to eat
	
	LinkedList<Runnable> eatEventListeners = new LinkedList<>();
	
	
	// ============ PUBLIC METHODS ============================
	
	/**
	 * Constructor from xml node
	 * @param xml
	 */
	public FeederModule(XML xml) {
		super(xml);
		maze = Experiment.get().maze;
		max_feeding_distance_sq = xml.hasAttribute("max_feeding_distance") ?
				xml.getFloatAttribute("max_feeding_distance") : 
				0.1f; // default distance is 10cm
		max_feeding_distance_sq *= max_feeding_distance_sq; // store the square value
		
	}
	
	/**
	 * 
	 * @return returns whether the robot was able to eat in the last simulation cycle
	 */
	public boolean ate() {
		return ate;
	}
	
	
	/**
	 * Order the robot to attempt eating from any feeder before performing any movement actions
	 * in the next simulation cycle.
	 */
	public void eatBeforeMotion() {
		eatingFeeder = canSubjectEat();
		if(eatingFeeder != null)
			simulator.addEventGeneratorSingleUse(()->{
				//check if still has food (somebody else ate from the feeder?)
				if(eatingFeeder.hasFood) {
					eatingFeeder.eat();
					for(var l : eatEventListeners) l.run();
				}		
			});
	}
	
	/**
	 * Orders the robot to attempt eating from any feeder after performing all movement actions
	 * in the next simulation cycle.
	 */
	public void eatAfterMotion() {
		simulator.addEventGeneratorSingleUse(()->{
			eatingFeeder = canSubjectEat();
			if(eatingFeeder!=null) {
				ate=true;
				eatingFeeder.eat();
				for(var l : eatEventListeners) l.run();				
			}
		});
	}
	
	
	/**
	 * The function checks whether the subject can eat from any feeder.
	 * If it can, the functions returns a feeder, otherwise it returns null
	 * @return A feeder from which the robot can eat or null if it can't eat from any feeder
	 */
	public Feeder canSubjectEat() {
		// get postion
		var pos = proxy.getPosition(); 
		
		// for each feeder, check whether the robot can eat or not
		for(var f : maze.feeders.values())
			if(canEat(f, pos)) 
				return f;
		return null;
	}
	
	
	
	
	@Override
	protected void clearEvents() {
		eatingFeeder = null;
		ate = false;
	}
	
	
	public void addEatEventListener(Runnable r) {
		eatEventListeners.add(r);
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
	
	@Override
	public String getDefaultName() {
		return "FeederModule";
	}
	
}
