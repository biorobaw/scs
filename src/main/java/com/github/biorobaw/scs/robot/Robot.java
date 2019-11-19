package com.github.biorobaw.scs.robot;

import java.util.HashMap;
import java.util.LinkedList;

import com.github.biorobaw.scs.simulation.object.RobotProxy;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;

/**
 * Class that implements a robot.
 * The class has a proxy to the robot and a set of robot modules.
 * @author bucef
 *
 */
public class Robot {
	
	RobotProxy robot_proxy = null;
	HashMap<String, RobotModule> modules = new HashMap<>();
	public LinkedList<Script> scripts = new LinkedList<>();
	
	public Robot(XML xml) {
		// TODO Auto-generated constructor stub
		robot_proxy = (RobotProxy)xml.getChild("robot_proxy").loadObject();
		for(var m : xml.getChildren("module")) {
			var module = m.<RobotModule>loadObject();
			module.setRobotProxy(robot_proxy);
			modules.put(module.getID(), module);
		}
	}
	
	/**
	 * Adds a module to the set of modules of the robot.
	 * If the module implements a Script, it is also added
	 * the list of Scripts of the robot.
	 * Modules that are scripts are added to the script scheduler at
	 * the beginning of each episode.
	 * @param name	name of the module to be added
	 * @param m		module to be added
	 */
	public void addModule(String name, RobotModule m) {
		modules.put(name, m);
		m.setRobotProxy(robot_proxy);
		if(m.runsScript()) scripts.add(m);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getModule(String id) {
		return (T)modules.get(id);
	}
	
	/**
	 * 
	 * @return returns the guid of the robot proxy
	 */
	public long get_guid() {
		return robot_proxy.get_guid();
	}
	
	public RobotProxy getRobotProxy() {
		return robot_proxy;
	}
	
	/**
	 * Clears the events of all robot modules.
	 * Called by the model after each execution.
	 */
	public void clearEvents() {
		for (var m : modules.values()) m.clearEvents();
	}
	
}
