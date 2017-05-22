package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class MoveFromToActionPerformer extends Module {
	
	//Point3f[] actions; 
	AffordanceRobot robot;

	public MoveFromToActionPerformer(String name, Robot robot) {
		super(name);

		this.robot = (AffordanceRobot) robot;

	}

	
	public void run() {
		
		
		Point3f pos = ((Point3fPort)getInPort("position")).get();
		Point3f nextPos = ((Point3fPort)getInPort("nextPosition")).get();
		
		float dx = nextPos.x-pos.x;
		float dy = nextPos.y-pos.y;
		double theta = Math.atan2(dy,dx );
		
		float distance = (float)Math.sqrt(dx*dx+dy*dy);
		
		
		//System.out.println("performing: "+action);
		float deltaAngle = GeomUtils.relativeAngle((float)theta, ((LocalizableRobot)robot).getOrientationAngle());
		robot.executeAffordance(new TurnAffordance(deltaAngle, distance));
		robot.executeAffordance(new ForwardAffordance(distance));
		
		EatAffordance eat = new EatAffordance();
		if(robot.checkAffordance(eat)) robot.executeAffordance(eat);
		
		//System.out.println("iteration: " + i);
		//System.out.println("");
		
	
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
