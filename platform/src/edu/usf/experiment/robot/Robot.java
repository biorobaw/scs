package edu.usf.experiment.robot;

import java.util.LinkedHashMap;
import java.util.LinkedList;


public abstract class Robot {
	
	public LinkedList<RobotAction> pendingActions = new LinkedList<RobotAction>();
	public LinkedHashMap<String, Object> actionMessageBoard = new LinkedHashMap<String, Object>();

	/**
	 * 
	 */
	public void processPendingActions(){
		actionMessageBoard.clear();
		for(RobotAction a : pendingActions)
			processAction(a);
		pendingActions.clear();
	}

	public abstract void processAction(RobotAction action);
	
	public abstract void executeTimeStep(float deltaT);
	
}


