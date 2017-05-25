package edu.usf.vlwsim.universe;

import javax.vecmath.Vector3f;

import edu.usf.experiment.utils.ElementWrapper;

public class StepRobotVirtualUniverse extends VirtUniverse {

	private float turnAngle;
	private float forwardDistance;

	public StepRobotVirtualUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		turnAngle = 0;
		forwardDistance = 0;
	}

	public void stepMotion(){
		if (turnAngle != 0){
			rotateRobot(turnAngle);
			while (!canRobotMove(0, .1f)) // TODO: this mimics rotations until the wall is not in front anymore - this should be done by the model
				rotateRobot(turnAngle);
		}
				
		moveRobot(new Vector3f(forwardDistance, 0, 0));
		
		turnAngle = 0;
		forwardDistance = 0;
	}
	
	public void setTurnAngle(float turnAngle){
		this.turnAngle = turnAngle;
	}
	
	public void setForwardDistance(float forwardDistance){
		this.forwardDistance = forwardDistance;
	}
}
