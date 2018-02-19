package edu.usf.vlwsim.universe;

import com.vividsolutions.jts.geom.Coordinate;

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
		}
				
		moveRobot(new Coordinate(forwardDistance, 0));
		
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
