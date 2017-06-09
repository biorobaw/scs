package edu.usf.experiment.robot.specificActions;

import edu.usf.experiment.robot.RobotAction;

public class FeederTaxicAction extends RobotAction{
	
	public static String actionID = "FeederTaxicAction";
	
	public FeederTaxicAction(Integer id) {
		super(actionID,id);
	}
	
	public Integer id(){
		return (Integer)params.get(0);
	}
	
	public void setId(Integer id){
		params.set(0, id);
	}

	
}
