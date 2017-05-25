package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class MultipleTActionPerformer extends Module {
	AffordanceRobot affRobot;

	public MultipleTActionPerformer(String name, Robot robot) {
		super(name);

		this.affRobot = (AffordanceRobot) robot;
	}

	
	public void run() {
		
		int action = ((Int0dPort)getInPort("action")).get();
		//System.out.println("performing: "+action);
//		float deltaAngle = GeomUtils.relativeAngle(angles[action], ((LocalizableRobot)robot).getOrientationAngle());
//		robot.executeAffordance(new TurnAffordance(deltaAngle, stepSize));
//		robot.executeAffordance(new ForwardAffordance(stepSize));
		
		
		
//		EatAffordance eat = new EatAffordance();
//		if(fRobot.hasFoundFood()) 
//			fRobot.eat();
//		else {
//			adRobot.setDirection(angles[action]);
//			adRobot.setADStep(stepSize);
//		}
			

		affRobot.executeAffordance(affRobot.getPossibleAffordances().get(action));
//		System.out.println("Executed " + affRobot.getPossibleAffordances().get(action));
		//System.out.println("");
		
	
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
