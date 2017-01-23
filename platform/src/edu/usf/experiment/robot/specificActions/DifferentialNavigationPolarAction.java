package edu.usf.experiment.robot.specificActions;

import edu.usf.experiment.robot.RobotAction;

public class DifferentialNavigationPolarAction extends RobotAction{
	public DifferentialNavigationPolarAction(Float v, Float w) {
		super("differentialNavigationPolarAction",v,w);
	}
	
	public Float v(){
		return (Float)params.get(0);
	}
	
	public Float w(){
		return (Float)params.get(1);
	}
	
	
	public void setV(Float val){
		params.set(0, val);
	}
	
	public void setW(Float val){
		params.set(1, val);
	}
	
}
