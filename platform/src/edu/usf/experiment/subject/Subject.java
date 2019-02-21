package edu.usf.experiment.subject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;

public class Subject {

	private String name;
	private String group;
	private Model model;
	public Robot robot;
	
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
	
	static public Model load(ElementWrapper modelNode, Robot robot) {
		try {
			Constructor constructor;
			String name = modelNode.getChildText("name");
//			constructor = classBySimpleName.get(name).getConstructor(
//					String.class, String.class, ElementWrapper.class,
//					Robot.class);
			constructor = Class.forName(name).getConstructor(
					ElementWrapper.class,
					Robot.class);
			Model model = (Model) constructor.newInstance(modelNode.getChild("params"), robot);
			return model;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("Model did not load correctly");
		System.exit(-1);
		return null;
	}

}
