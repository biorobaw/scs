package edu.usf.ratsim.nsl.modules.actionselection;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

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
	private boolean normalize;

	
	
	public ProportionalVotes(String name, int numActions,boolean normalize) {
		super(name);
		this.numActions = numActions;
		actionVote = new float[numActions];
		addOutPort("votes", new Float1dPortArray(this, actionVote));
		//this.normalize = normalize;
	}
	
	public ProportionalVotes(String name, int numActions) {
		this(name, numActions, true); //default value of normalize defined as true to keep back compatibility
	};

	public void run() {
		Float1dSparsePort states = (Float1dSparsePort) getInPort("states");
		FloatMatrixPort value = (FloatMatrixPort) getInPort("value");
		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;

		double sum = 0;
		//float cantStates = states.getSize();
		for (int state : states.getNonZero().keySet()) {
			float stateVal = states.get(state);
			if (stateVal != 0) {
				double p1 =sum / (sum+stateVal);
				double p2 = stateVal / (sum+stateVal);
				for (int action = 0; action < numActions; action++) {
					float actionVal = value.get(state, action);
					if (actionVal != 0)
						actionVote[action] = (float)(p1*actionVote[action] + p2* actionVal);
				}
				sum += stateVal;
			}
		}


//		if (sum != 0)
//			for (int action = 0; action < numActions; action++)
//				// Normalize with real value and revert previous normalization
//				actionVote[action] = (float) (actionVote[action] / sum);

		if (Debug.printValues) {
			System.out.println("RL votes");
			for (int action = 0; action < numActions; action++)
				System.out.print(actionVote[action] + " ");
			System.out.println();
		}

	}
	
	public String voteString(){
		String res = "";
		for(float a : actionVote) res += a+", ";
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
