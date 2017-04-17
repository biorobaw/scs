package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

public class GradientVotes extends Module implements Voter {

	private float normalizer;
	public float[] actionVote;
	private int numActions;
	private boolean[] connected;
	private float foodReward;

	public GradientVotes(String name, int numActions, List<Float> connProbs,
			List<Integer> statesPerLayer, float normalizer, float foodReward) {
		super(name);

		actionVote = new float[numActions];
		addOutPort("votes", new Float1dPortArray(this, actionVote));

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
		
		this.normalizer = normalizer;
		this.foodReward = foodReward;
	}

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		Float2dPort value = (Float2dPort) getInPort("value");
		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;

		double sum = 0;
		for (Integer state : states.getNonZero().keySet()) {
			if (connected[state]) {
				float stateVal = states.get(state);
				if (stateVal != 0) {
					sum += stateVal;
					for (int action = 0; action < numActions; action++) {
						float actionVal = value.get(state, action);
						if (actionVal != 0)
							actionVote[action] = actionVote[action] + stateVal
									* actionVal;
					}
				}
			}
		}

		// Normalize
		for (int action = 0; action < numActions; action++)
			// Normalize with real value and revert previous normalization
			actionVote[action] = (float) (actionVote[action] / normalizer);
		
		for (int action = 0; action < numActions; action++)
			// Normalize with real value and revert previous normalization
			if (Math.abs(actionVote[action]) > foodReward)
				actionVote[action] = Math.signum(actionVote[action])*foodReward;

		if (Debug.printValues) {
			System.out.println("RL votes");
			for (int action = 0; action < numActions; action++)
				System.out.print(actionVote[action] + " ");
			System.out.println();
		}
	}

	public float[] getVotes() {
		return actionVote;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
