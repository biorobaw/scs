package edu.usf.ratsim.nsl.modules.rl;

public class History {

	public float[] getStatesBefore() {
		return statesBefore;
	}

	public float[] getStatesAfter() {
		return statesAfter;
	}

	public float[] getActionsVotes() {
		return actionsVotes;
	}

	public int getAction() {
		return action;
	}

	public float getReward() {
		return reward;
	}

	float[] statesBefore;
	float[] statesAfter;
	float[] actionsVotes;
	int action;
	float reward;

	public History(float[] statesBefore, float[] statesAfter,
			float[] actionVotes, int action, float reward) {
		super();
		this.statesBefore = statesBefore;
		this.statesAfter = statesAfter;
		this.actionsVotes = actionVotes;
		this.action = action;
		this.reward = reward;
	}

}
