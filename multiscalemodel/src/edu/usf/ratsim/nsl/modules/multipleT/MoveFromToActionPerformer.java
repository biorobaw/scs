package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
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
	RobotOld robot;
	SubjectOld subject;

	public MoveFromToActionPerformer(String name,SubjectOld subject) {
		super(name);

		this.subject = subject;
		this.robot = subject.getRobot();

	}

	
	public void run() {
		
		subject.setHasEaten(false);
		subject.clearTriedToEAt();
		Point3f pos = ((Point3fPort)getInPort("position")).get();
		Point3f nextPos = ((Point3fPort)getInPort("nextPosition")).get();
		
		float dx = nextPos.x-pos.x;
		float dy = nextPos.y-pos.y;
		double theta = Math.atan2(dy,dx );
		
		float distance = (float)Math.sqrt(dx*dx+dy*dy);
		
		
		//System.out.println("performing: "+action);
		float deltaAngle = GeomUtils.relativeAngle((float)theta, ((LocalizableRobot)robot).getOrientationAngle());
		robot.executeAffordance(new TurnAffordance(deltaAngle, distance), subject);
		robot.executeAffordance(new ForwardAffordance(distance), subject);
		
		EatAffordance eat = new EatAffordance();
		if(robot.checkAffordance(eat)) robot.executeAffordance(eat, subject);
		
		//System.out.println("iteration: " + i);
		//System.out.println("");
		
	
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
