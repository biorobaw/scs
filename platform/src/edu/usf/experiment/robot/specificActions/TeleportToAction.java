package edu.usf.experiment.robot.specificActions;

import edu.usf.experiment.robot.RobotAction;

public class TeleportToAction extends RobotAction{
	
	public static String actionID = "moveTo";
	
	public TeleportToAction(Float x, Float y, Float z, Float w) {
		super("moveTo",x,y,z,w);
	}
	
	public Float x(){
		return (Float)params.get(0);
	}
	
	public Float y(){
		return (Float)params.get(1);
	}
	
	public Float z(){
		return (Float)params.get(2);
	}
	
	public Float theta(){
		return (Float)params.get(3);
	}
	
	public void setX(Float val){
		params.set(0, val);
	}
	
	public void setY(Float val){
		params.set(1, val);
	}
	
	public void setZ(Float val){
		params.set(2, val);
	}
	
	public void setTheta(Float val){
		params.set(3, val);
	}
	
}
