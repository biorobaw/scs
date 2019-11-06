package com.github.biorobaw.scs.model;


import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.robot.Robot;
import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.XML;

public abstract class Model implements Script{
	
	protected Robot robot;
	private   AbstractSimulator simulator = Experiment.get().simulator;
	public    String subject_id;
	
	public Model(XML xml) {
		this.subject_id = xml.getName();
		this.robot= xml.getChild("robot")
					   .setAttribute("subject_id", subject_id)
					   .loadObject();
		robot.getRobotProxy().addToSimulation();
	}
	
	@Override
	public int getPriority() {
		return 20;
	}
	
	/**
	 * Function that implements the logic of the model
	 * @return Returns the amount of simulated time in ms to wait until next execution
	 */
	public abstract long runModel();
	
	/**
	 * Implements a default run function for the models
	 */
	@Override
	final public void run() {
		long next_wait_ms = runModel();
		robot.clearEvents();		
		simulator.schedule(next_wait_ms, this);
	}
	
	/**
	 * 
	 * @return returns the robot of the model
	 */
	public Robot getRobot() {
		return robot;
	}
	

}
