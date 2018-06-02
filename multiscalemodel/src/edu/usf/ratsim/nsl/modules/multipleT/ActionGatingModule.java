package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.List;

import edu.usf.experiment.robot.AbsoluteDirectionRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.vlwsim.universe.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class ActionGatingModule extends Module {
	
	public float[] probabilities;

	private AffordanceRobot robot;
	int numActions ;

	public ActionGatingModule(String name, Robot robot,int numActions) {
		super(name);
		this.numActions = numActions;
		this.robot = (AffordanceRobot) robot;
		
		probabilities = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));

	}

	
	public void run() {
		Float1dPortArray input = (Float1dPortArray) getInPort("input");
		
		List<Affordance> aff = robot.getPossibleAffordances();
		aff = robot.checkAffordances(aff);
		
		float sum = 0;
		for (int i =0;i<numActions;i++)
		{
			probabilities[i] =  (float)(input.get(i)*aff.get(i).getRealizable());
			sum += probabilities[i];
		}
		
		// Normalize - its probabilities
		for (int i =0;i<numActions;i++)
		{
			probabilities[i] /= sum;
		}
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
