package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * 
 * @author biorob
 * 
 */
public class ActionFromProbabilities extends Module {	
	
	Random rand = RandomSingleton.getInstance();
	Int0dPort outport  = new Int0dPort(this);

	public ActionFromProbabilities(String name) {
		super(name);

		addOutPort("action", outport);

	}

	public void run() {
		Float1dPortArray input = (Float1dPortArray) getInPort("probabilities");

		float u = rand.nextFloat();
		int i=1;
		for (float sum = input.get(0);  sum < u;  i++)
			sum+=input.get(i);
		outport.set(i-1);

	}


	@Override
	public boolean usesRandom() {
		return true;
	}
}
