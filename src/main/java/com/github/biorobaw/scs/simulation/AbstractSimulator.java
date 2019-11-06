package com.github.biorobaw.scs.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.simulation.object.SimulatedObject;
import com.github.biorobaw.scs.simulation.scripts.Scheduler;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.Debug;
import com.github.biorobaw.scs.utils.XML;

public abstract class AbstractSimulator {
	
	// ========== STATIC MEMBERS =======================================
	
	// singleton instance and getter function
	public final static Vector3D zVector = new Vector3D(0,0,1);
	

	// ============= SIMULATOR VARIABLES ==============================
	
	public HashMap<String,Object> properties = new HashMap<>(); // map of properties	
	Scheduler<Runnable> scriptScheduler = new Scheduler<>(); // schedules scripts to be executed
	long simulation_step_ms = 30;
	LinkedList<Runnable> permanentEventGenerators = new LinkedList<>();
	LinkedList<Runnable> singleUseEventGenerators = new LinkedList<>();
	
	// ============= SIMULATOR METHODS ===============================
	
	public AbstractSimulator(XML xml) {
		if(xml.hasAttribute("simulation_step_ms"))
			simulation_step_ms = xml.getLongAttribute("simulation_step_ms");
		
		// set the simulation speed, by default it runs at max speed
		if(xml.hasAttribute("simulation_speed"))
			SimulationControl
				.setSimulationSpeed(xml.getIntAttribute("simulation_speed"));
	}
	
	/**
	 * Advances the simulation by executing all scripts and then
	 * performing the physical computations for the next cycle.
	 * @param delta_ms
	 */
	private void advanceTime(long delta_ms) {
		
		// profiling stamp
		long stamp = Debug.tic();
		
		// run all scripts scheduled before next_time
		long next_time = getTime() + delta_ms;
//		System.out.println("scripts empty: "+ scriptScheduler.isEmpty());
//		System.out.println("next time: " + scriptScheduler.nextTime());
		while(!scriptScheduler.isEmpty() && scriptScheduler.nextTime() < next_time){
			scriptScheduler.pop().run();
		}
		
		// display profiling results
		if(Debug.profiling) System.out.println("Model time: " + Debug.toc(stamp));
		
		// update display and wait if paused
		SimulationControl.waitIfPaused();  // check pause before simulating
		
		// perform simulation
		simulate(delta_ms); 
		
		// generate events:
		for(var g : singleUseEventGenerators) g.run();
		for(var g : permanentEventGenerators) g.run();
		singleUseEventGenerators.clear();
	}
	
	/**
	 * Advances the simulation by executing all scripts and then
	 * performing the physical computations for the next cycle.
	 * The amount of time simulated is the default simulation step
	 */
	public void advanceTime() {
		advanceTime(simulation_step_ms);
	}
	
	
	/**
	 * Function called by scripts to wait for next time (by rescheduling their execution)
	 * @param delta_ms
	 * @param script
	 */
	public void schedule(long delta_ms, Runnable script) {
		if(delta_ms == 0)
			scriptScheduler.squedule(script, getTime() + simulation_step_ms);
		else if(delta_ms > 0) 
			scriptScheduler.squedule(script, getTime() + delta_ms);
	}
	
	/**
	 * Schedule a script for execution.
	 * @param delta_ms  specifies in how many simulated ms will the script execute
	 * @param priority	specifies the priority of the script
	 * @param script
	 */
	public void addScript(Runnable script, long delta_ms, int priority) {
		scriptScheduler.setPriority(script, priority);
		schedule(delta_ms,script);
	}
	
	/**
	 * Function that clears all scheduled scripts/tasks
	 */
	public void clearScripts() {
		scriptScheduler.clear();
	}
	
	/**
	 * Adds a set of scripts to be executed. 
	 * @param scripts The set of scripts to be executed
	 */
	public void addInitialScripts(Collection<? extends Script> scripts) {
		for(var s : scripts) addScript(s, s.getInitialSchedule(), s.getPriority());
	}
	
	/**
	 * Adds a script to be executed. 
	 * @param script The script to be executed
	 */
	public void addInitialScript(Script s) {
		addScript(s, s.getInitialSchedule(), s.getPriority());
	}
	
	/**
	 * Adds a single use event generator
	 * @param generator the generator to be added
	 */
	public void addEventGeneratorSingleUse(Runnable generator) {
		singleUseEventGenerators.add(generator);
	}
	
	/**
	 * Adds a permanent event generator
	 * @param generator
	 */
	public void addEventGeneratorPermanent(Runnable generator) {
		permanentEventGenerators.add(generator);
	}
	
	// ========= ABSTRACT FUNCTIONS REQUIRED FOR SIMULATION ===============

	/**
	 * Adds a simulated object in the given position
	 * Function must set the object guid assigned by the simulator
	 * @param o			simulated object to be added
	 * @param position	position of the object
	 * @param orientation orientation of the robot
	 */
	public abstract void addObject(SimulatedObject o, Vector3D position, Rotation orientation);
	
	/**
	 * Remove and object from the simulator.
	 * @param o simulated object to be removed
	 */
	public abstract void removeObject(SimulatedObject o);
	
	/**
	 * Returns the position of an object in the simulator
	 * @param guid	The simulated object identifier in the simulator
	 * @return	Position of the object
	 */
	public abstract Vector3D getObjectPosition(long guid);
	
	/**
	 * Returns the orientation of an object in the simulator
	 * @param guid	The simulated object identifier in the simulator
	 * @return	Rotation representing the orientation of the object
	 */
	public abstract Rotation getObjectOrientation(long guid);
	
	/**
	 * Sets/changes the position of an object in the simulator
	 * @param guid	The simulated object identifier in the simulator
	 * @param pos	The new position of the object
	 */
	public abstract void setObjectPosition(long guid, Vector3D position);
	
	/**
	 * Sets/changes the orientation of an object in the simulator
	 * @param guid	The simulated object identifier in the simulator
	 * @param pos	The new orientation of the object
	 */
	public abstract void setObjectOrientation(long guid, Rotation orientation);
	
	/**
	 * Advances the simulation by a given amount of time.
	 * @param time_ms The amoung of time in milliseconds
	 */
	public abstract void simulate(long time_ms);
	
	/**
	 * Returns the simulated time in milliseconds
	 * @return
	 */
	public abstract long getTime();
	
	
}
