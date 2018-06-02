package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Map;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Class to set the votes for actions depending both in the state activation and
 * a value function.
 * 
 * @author ludo
 *
 */
public class ProportionalValue extends Module {

	public Float0dPort valuePort;
	private float foodReward;

	public ProportionalValue(String name, float foodReward) {
		super(name);
		valuePort = new Float0dPort(this);
		addOutPort("value",valuePort);
		
		this.foodReward = foodReward;
	}

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		Float2dPort value = (Float2dPort) getInPort("value");
		
		run(states.getNonZero(), value);
	}
	

	public void run(Map<Integer, Float> states, Float2dPort value) {
		float valueEst = 0f;

		float sum = 0;
		for (int state : states.keySet()) {
			sum += states.get(state);
		}
		
		for (Integer s : states.keySet()){
			float stateVal = states.get(s) / sum;
			if (stateVal != 0) {
				float val = value.get(s, 0);
				if (val != 0)
					valueEst += stateVal * val;
			}
		}
		
		// Max value is food reward
		valueEst = Math.max(-foodReward, Math.min(valueEst, foodReward));
		
		valuePort.set(valueEst);
		
		if (Debug.printValues) {
			System.out.println("RL value");
			System.out.print(valueEst + " ");
			System.out.println();
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
