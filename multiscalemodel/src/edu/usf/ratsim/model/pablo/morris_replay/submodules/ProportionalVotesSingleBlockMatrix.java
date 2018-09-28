package edu.usf.ratsim.model.pablo.morris_replay.submodules;

import java.util.Map;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.Voter;

/**
 * Class to set the votes for actions depending both in the state activation and
 * a value function.
 * 
 * @author ludo
 *
 */
public class ProportionalVotesSingleBlockMatrix extends Module implements Voter {

	public float[] actionVote;
	private int numActions;

	public ProportionalVotesSingleBlockMatrix(String name, int numActions) {
		super(name);
		this.numActions = numActions;
		actionVote = new float[numActions];
		addOutPort("votes", new Float1dPortArray(this, actionVote));
	}

	public void run() {
		Float2dSingleBlockMatrixPort states = (Float2dSingleBlockMatrixPort) getInPort("states");
		Float2dPort value = (Float2dPort) getInPort("qValues");

		run(states, value);

	}

	public void run(Float2dSingleBlockMatrixPort states, Float2dPort value) {
		for (int action = 0; action < numActions; action++)
			actionVote[action] = 0f;
		
		
		float sum = 0;
		for(int i=0;i<states.getBlockRows();i++)
			for(int j=0;j<states.getBlockCols();j++){
				sum+=states.getBlock(i, j);
			}
		
		
		// float cantStates = states.getSize();
		for(int i=0;i<states.getBlockRows();i++)
			for(int j=0;j<states.getBlockCols();j++) {
				float stateVal = states.getBlock(i, j) / sum;
				if (stateVal != 0) {
					int id = states.getBlockIndex(i, j);
					for (int action = 0; action < numActions; action++) {
						float actionVal = value.get(id, action);
						if (actionVal != 0)
							actionVote[action] += (float) (stateVal * actionVal);
					}
				}
			}

		
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
