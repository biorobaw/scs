package edu.usf.experiment.robot;

import java.util.LinkedList;


public abstract class Robot {
	
	public LinkedList<RobotAction> pendingActions = new LinkedList<RobotAction>();

	/**
	 * 
	 */
	public void processPendingActions(){
		for(RobotAction a : pendingActions)
			processAction(a);
		pendingActions.clear();
	}

	public abstract void processAction(RobotAction action);
	
	public abstract void executeTimeStep(float deltaT);
	
}


