package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class MultipleTActionPerformer extends Module {
	int numActions;	
	
	float stepSize;
	//Point3f[] actions; 
	float[] angles;
	AffordanceRobot robot;

	public MultipleTActionPerformer(String name,int numActions,float stepSize, Robot robot) {
		super(name);

		this.numActions = numActions;
		this.angles = new float[numActions];
		//this.actions = new Point3f[numActions];
		this.robot = (AffordanceRobot) robot;
		this.stepSize = stepSize;
		
		double deltaAngle = 2*Math.PI/numActions;
		float angle = 0;
		for (int i=0;i<numActions;i++)
		{	
			//actions[i] =new Point3f((float)Math.cos(angle)*stepSize,(float)Math.sin(angle)*stepSize,0);
			angles[i] = angle;
			angle+=deltaAngle;
		}

	}

	
	public void run() {
		
		int action = ((Int0dPort)getInPort("action")).get();
		//System.out.println("performing: "+action);
		float deltaAngle = GeomUtils.relativeAngle(angles[action], ((LocalizableRobot)robot).getOrientationAngle());
		robot.executeAffordance(new TurnAffordance(deltaAngle, stepSize));
		robot.executeAffordance(new ForwardAffordance(stepSize));
		
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
