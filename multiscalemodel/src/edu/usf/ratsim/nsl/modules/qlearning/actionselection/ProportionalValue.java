package edu.usf.ratsim.nsl.modules.qlearning.actionselection;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.Float1dPort;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.FloatMatrixPort;
import edu.usf.micronsl.Module;
import edu.usf.ratsim.nsl.modules.Voter;

/**
 * Class to set the votes for actions depending both in the state activation and
 * a value function.
 * 
 * @author ludo
 *
 */
public class ProportionalValue extends Module implements Voter {

	public float[] valueEst;
	private int numActions;

	public ProportionalValue(String name, int numActions) {
		super(name);
		valueEst = new float[1];
		this.numActions = numActions;
		addOutPort("valueEst", new Float1dPortArray(this, valueEst));
	}

	public void run() {
		Float1dPort states = (Float1dPort) getInPort("states");
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
		valueEst[0] = 0f;

		double sum = 0;
		float cantStates = states.getSize();
		for (int state = 0; state < cantStates; state++) {
			float stateVal = states.get(state);
			if (stateVal != 0) {
				sum += stateVal;
				float val = value.get(state, numActions);
				if (val != 0)
					valueEst[0] = valueEst[0] + stateVal * val;
			}
		}

		// Normalize
		if (sum != 0)
			valueEst[0] = (float) (valueEst[0] / sum);

		if (Debug.printValues) {
			System.out.println("RL value");
			System.out.print(valueEst[0] + " ");
			System.out.println();
		}

	}

	@Override
	public float[] getVotes() {
		return valueEst;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
