package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

public class GradientValue extends Module implements Voter {

	private float normalizer;
	public float[] valueEst;
	private int numActions;
	private boolean[] connected;
	private float foodReward;

	public GradientValue(String name, int numActions, List<Float> connProbs,
			List<Integer> statesPerLayer, float normalizer, float foodReward) {
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
			int numConnected = 0;
			float prob = connProbs.get(layer);
			for (int i = 0; i < layerNumStates; i++) {
				connected[stateIndex] = r.nextFloat() < prob;
				if (connected[stateIndex])
					numConnected++;
				stateIndex++;
			}
			layer++;
		}
		
		this.normalizer = normalizer;
		this.foodReward = foodReward;
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
						System.out.println(actionVal);
						System.out.println("Numeric Error in Gradient value");
						System.exit(1);
					}
				}
			}
		}

		// Normalize
		valueEst[0] = (float) (valueEst[0] / normalizer);
		if (valueEst[0] > foodReward)
			valueEst[0] = foodReward;
		
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

	public boolean isConnected(int k) {
		return connected[k];
	}

	public boolean[] getConnectPattern() {
		return connected;
	}

}
