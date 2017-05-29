package edu.usf.ratsim.nsl.modules.actionselection;

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
public class MaxAffordanceActionPerformer extends Module {
	AffordanceRobot affRobot;

	public MaxAffordanceActionPerformer(String name, Robot robot) {
		super(name);

		this.affRobot = (AffordanceRobot) robot;
	}

	
	public void run() {
		int action = ((Int0dPort)getInPort("action")).get();

		affRobot.executeAffordance(affRobot.getPossibleAffordances().get(action));
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
