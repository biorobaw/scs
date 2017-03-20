package edu.usf.experiment.subject;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Subject {
	protected String name;
	protected String group;
	protected Robot robot;
	public boolean hasEaten;
	
	public Subject(){};
	
	static public Subject instance;
	
	public Subject(String name, String group, ElementWrapper modelParams,Robot robot) {
		this.name = name;
		this.group = group;
		this.robot = robot;
		System.out.println("group: " + group);
		hasEaten = false;
		instance = this;

	}
	
	public void save(){};
	
	/**
	 * Returns the name of the subject
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}
	
	/**
	 * Returns whether the subject has eaten in the last iteration
	 * @return
	 */
	public boolean hasEaten(){
		return hasEaten;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setName(String subjectName) {
		this.name = subjectName;
	}
	
	public Robot getRobot() {
		return robot;
	}
	
	public void setHasEaten(boolean b) {
		hasEaten = b;
	}
	
	
	
	/**
	 * Advances one cycle in the internal model of the brain usually resulting
	 * in a decision being taken
	 */
	public abstract void stepCycle();
	
	public void newEpisode(){
		hasEaten = false;
		
	}
	
	public abstract void newTrial();
	
	public void endEpisode(){}
	
	
	
	
	

}
