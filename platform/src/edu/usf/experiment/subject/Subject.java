package edu.usf.experiment.subject;

import edu.usf.experiment.robot.Robot;
import edu.usf.micronsl.Model;

public class Subject {

	private String name;
	private String group;
	private Model model;
	private Robot robot;
	
	public Subject(String name, String group, Model model, Robot robot) {
		this.name = name;
		this.group = group;
		this.model = model;
		this.robot = robot;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setName(String subjectName) {
		this.name = subjectName;
	}
	
	public void load(){
		
	}
	public void save(){
		
	}

}
