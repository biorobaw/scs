package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class ClosestActionSelection extends Module {
	int numActions;	
	
	float stepSize;
	//Point3f[] actions; 
	double deltaAngle;
	
	Int0dPort action = new Int0dPort(this);

	public ClosestActionSelection(String name,int numActions) {
		super(name);

		this.numActions = numActions;
		this.deltaAngle = 2*Math.PI/numActions;
		this.addOutPort("action", action);

	}

	
	public void run() {
		
		Point3f pos = ((Point3fPort)getInPort("position")).get();
		Point3f nextPos = ((Point3fPort)getInPort("nextPosition")).get();
		
		double theta = Math.atan2(nextPos.y-pos.y,nextPos.x-pos.x );
		if (theta < 0) theta+=(2*Math.PI);
		action.set((int)(Math.round(theta/deltaAngle) % numActions));
		

	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
