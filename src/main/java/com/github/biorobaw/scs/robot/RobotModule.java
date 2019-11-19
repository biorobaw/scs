package com.github.biorobaw.scs.robot;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;

public abstract class RobotModule implements Script {
	
	protected RobotProxy proxy;
	protected AbstractSimulator simulator;
	protected String module_id;
	
	
	public RobotModule(XML xml) {
		this.simulator = Experiment.get().simulator;
		module_id = xml.hasAttribute("id") ? xml.getId() : getDefaultName();
	}
	
	public void setRobotProxy(RobotProxy proxy) {
		this.proxy = proxy;
	}
	
	/**
	 * Default dummy run function.
	 */
	public void run() {
		
	}
	
	public boolean runsScript() {
		return false;
	}
	
	@Override
	public int getPriority() {
		return 30; // Default priority for robot modules
	}

	/**
	 * Clears events produced by the module.
	 * Function called by the robot or model.
	 */
	protected void clearEvents() {		
	}
	
	/**
	 * 
	 * @return Returns a string representing the id of the module if no id was specified
	 */
	abstract public String getDefaultName() ;
	
	/**
	 * 
	 * @return returns the id of the module
	 */
	public String getID() {
		return module_id;
	}
	
}
