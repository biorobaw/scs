package edu.usf.ratsim.nsl.modules.qlearning.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Float1dSparsePort;
import edu.usf.ratsim.micronsl.FloatMatrixPort;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.nsl.modules.Voter;

public class GradientValue extends Module implements Voter {

	private static final float NORMALIZER = 50f;
	public float[] valueEst;
	private int numActions;
	private boolean[] connected;

	public GradientValue(String name, int numActions, List<Float> connProbs,
			List<Integer> statesPerLayer) {
		super(name);

		valueEst = new float[1];
		addOutPort("valueEst", new Float1dPortArray(this, valueEst));

		this.numActions = numActions;

		int numStates = 0;
		for (Integer stateLen : statesPerLayer)
			numStates += stateLen;
		connected = new boolean[numStates];
		Random r = RandomSingleton.getInstance();
		int layer = 0;
		int stateIndex = 0;
		for (Integer layerNumStates : statesPerLayer) {
			float prob = connProbs.get(layer);
			for (int i = 0; i < layerNumStates; i++) {
				connected[stateIndex] = r.nextFloat() < prob;
				stateIndex++;
			}
			layer++;
		}
	}

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
		valueEst[0] = 0f;

		double sum = 0;
		for (Integer state : states.getNonZero().keySet()) {
			if (connected[state]) {
				float stateVal = states.get(state);
				if (stateVal != 0) {
					sum += stateVal;
					float actionVal = value.get(state, numActions);
					if (actionVal != 0)
						valueEst[0] = valueEst[0] + stateVal * actionVal;
					if (Float.isInfinite(valueEst[0]) || Float.isNaN(valueEst[0])) {
						System.out.println("Numeric Error in Gradient value");
						System.exit(1);
					}
				}
			}
		}

		// Normalize
		valueEst[0] = (float) (valueEst[0] / NORMALIZER);
		
		if (Float.isInfinite(valueEst[0]) || Float.isNaN(valueEst[0])) {
			System.out.println("Numeric Error in Gradient value");
			System.exit(1);
		}

		if (Debug.printValues) {
			System.out.println("RL value");
			System.out.print(valueEst[0] + " ");
			System.out.println();
		}
	}

	public float[] getVotes() {
		return valueEst;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
