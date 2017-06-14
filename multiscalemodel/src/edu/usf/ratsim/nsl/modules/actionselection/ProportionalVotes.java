package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Map;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

/**
 * Class to set the votes for actions depending both in the state activation and
 * a value function.
 * 
 * @author ludo
 *
 */
public class ProportionalVotes extends Module implements Voter {

	public float[] actionVote;
	private int numActions;
	private float foodReward;

	public ProportionalVotes(String name, int numActions, float foodReward) {
		super(name);
		this.numActions = numActions;
		actionVote = new float[numActions];
		addOutPort("votes", new Float1dPortArray(this, actionVote));
		this.foodReward = foodReward;
	}

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		Float2dPort value = (Float2dPort) getInPort("qValues");

		run(states.getNonZero(), value);

	}

	public void run(Map<Integer, Float> nonZero, Float2dPort value) {
		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;
		
		
		float sum = 0;
		for (int state : nonZero.keySet()) {
			sum += nonZero.get(state);
		}
		// float cantStates = states.getSize();
		for (int state : nonZero.keySet()) {
			float stateVal = nonZero.get(state) / sum;
			if (stateVal != 0) {
				for (int action = 0; action < numActions; action++) {
					float actionVal = value.get(state, action);
					if (actionVal != 0)
						actionVote[action] += (float) (stateVal * actionVal);
				}
			}
		}

//		 Max value is food reward
		for (int action = 0; action < numActions; action++)
			actionVote[action] = Math.max(-foodReward, Math.min(actionVote[action], foodReward));
		
		if (Debug.printValues) {
			System.out.println("RL votes");
			for (int action = 0; action < numActions; action++)
				System.out.print(actionVote[action] + " ");
			System.out.println();
		}
	}

	public String voteString() {
		String res = "";
		for (float a : actionVote)
			res += a + ", ";
		return res;
	}

	@Override
	public float[] getVotes() {
		return actionVote;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
