package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
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
	TeleportRobot teleRobot;
	private FeederRobot fRobot;

	public MoveFromToActionPerformer(String name, Robot robot) {
		super(name);
		
		this.teleRobot = (TeleportRobot) robot;
		this.fRobot = (FeederRobot) robot;
	}

	
	public void run() {
		
		
		Point3f pos = ((Point3fPort)getInPort("position")).get();
		Point3f nextPos = ((Point3fPort)getInPort("nextPosition")).get();
		
//		float dx = nextPos.x-pos.x;
//		float dy = nextPos.y-pos.y;
//		double theta = Math.atan2(dy,dx );
//		
//		float distance = (float)Math.sqrt(dx*dx+dy*dy);
//		
//		
//		//System.out.println("performing: "+action);
//		float deltaAngle = GeomUtils.relativeAngle((float)theta, ((LocalizableRobot)robot).getOrientationAngle());
//		robot.executeAffordance(new AbsoluteAngleAffordance(theta, distance));
//		robot.executeAffordance(new TurnAffordance(deltaAngle, distance));
//		robot.executeAffordance(new ForwardAffordance(distance));
//		
		teleRobot.setPosition(nextPos);
		
		
//		if(fRobot.hasFoundFood()) fRobot.eat();
		
		//System.out.println("iteration: " + i);
		//System.out.println("");
		
	
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
