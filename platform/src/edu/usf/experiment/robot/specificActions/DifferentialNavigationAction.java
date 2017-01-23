package edu.usf.experiment.robot.specificActions;

import edu.usf.experiment.robot.RobotAction;

public class DifferentialNavigationAction extends RobotAction{
	public DifferentialNavigationAction(Float leftSpeed, Float rightSpeed) {
		super("differentialNavigationAction",leftSpeed,rightSpeed);
	}
	
	public Float left(){
		return (Float)params.get(0);
	}
	
	public Float right(){
		return (Float)params.get(1);
	}
	
	
	public void setLeft(Float val){
		params.set(0, val);
	}
	
	public void setRight(Float val){
		params.set(1, val);
	}
	
}
