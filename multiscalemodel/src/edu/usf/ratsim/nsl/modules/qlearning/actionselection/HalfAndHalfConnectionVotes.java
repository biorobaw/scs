package edu.usf.ratsim.nsl.modules.qlearning.actionselection;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.Float1dPort;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.FloatMatrixPort;
import edu.usf.micronsl.Module;
import edu.usf.ratsim.nsl.modules.Voter;

public class HalfAndHalfConnectionVotes extends Module implements Voter {

	public float[] actionVote;
	private int numActions;
	private float cellContribution;
	private float[] stateData;

	public HalfAndHalfConnectionVotes(String name, int numVotes, float cellContribution) {
		super(name);
		actionVote = new float[numVotes];
		addOutPort("votes", new Float1dPortArray(this, actionVote));
		this.numActions = numVotes;
		this.cellContribution = cellContribution;
		stateData = null;
	}

	public void run() {
		Float1dPort states = (Float1dPort) getInPort("states");
		if (stateData == null)
			stateData = new float[states.getSize()];
		states.getData(stateData);
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");

		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;

		float sumActionSel = 0;
		int cantStates = states.getSize();
		// System.out.println(cantStates);
		for (int state = 0; state < cantStates / 2; state++) {
			float stateVal = stateData[state];
			// Update gradient every some steps
			if (stateVal != 0) {
				sumActionSel += stateVal;
				for (int action = 0; action < numActions; action++) {
					float actionVal = value.get(state, action);
					if (actionVal != 0){
						// action selection contributes only in smaller states
						// (smaller scales
						actionVote[action] = actionVote[action] + cellContribution * stateVal
								* actionVal;
//						sumActionSel += stateVal * actionVal;
//						System.out.println(actionVote[action]);
					}
					
				}
			}
		}

//		for (int action = 0; action < numActions; action++)
//			if (Math.abs(actionVote[action]) > 1)
//				actionVote[action] = 1 * Math.signum(actionVote[action]);
		
		// Normalize
		if (sumActionSel != 0)
			for (int action = 0; action < numActions; action++)
				// Normalize with real value and revert previous normalization
				actionVote[action] = actionVote[action] / sumActionSel;
		
		if (Debug.printHalfAndHalf) {
			System.out.println("RL Half and Half votes");
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
