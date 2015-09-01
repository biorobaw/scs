package edu.usf.ratsim.nsl.modules.qlearning.actionselection;

import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Float1dPort;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.FloatMatrixPort;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.micronsl.Port;
import edu.usf.ratsim.nsl.modules.Voter;

public class HalfAndHalfConnectionValue extends Module implements Voter {

	public float[] value;
	private int numActions;
	private float[] stateData;
	private float cellContribution;

	public HalfAndHalfConnectionValue(String name, int numActions, float cellContribution) {
		super(name);
		value = new float[1];
		addOutPort("valueEst", new Float1dPortArray(this, value));
		this.numActions = numActions;
		this.cellContribution = cellContribution;
		stateData = null;
	}

	public void run() {
		Float1dPort states = (Float1dPort) getInPort("states");
		if (stateData == null)
			stateData = new float[states.getSize()];
		states.getData(stateData);
		FloatMatrixPort rlValue = (FloatMatrixPort) getInPort("value");

		value[0] = 0f;
		int cantStates = states.getSize();
		float sumValue = 0;
		for (int state = cantStates / 2; state < cantStates; state++) {
			float valueVal = rlValue.get(state, numActions);
			float stateVal = stateData[state];
			sumValue += stateVal;
//			sumValue += stateVal * valueVal;
			if (valueVal != 0 )
				value[0] = value[0] + cellContribution * stateVal
						* valueVal;
		}
		
//		if (Math.abs(value[0]) > 1)
//			value[0] = 1 * Math.signum(value[0]);

		if (sumValue != 0)
			value[0] = value[0] / sumValue;

		if (Debug.printHalfAndHalf) {
			System.out.println("RL Half and Half value: " + value[0]);
		}

	}

	public float[] getVotes() {
		return value;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
