package edu.usf.ratsim.nsl.modules.qlearning;

public final class StateActionReward {
	private int state;
	private int action;
	private float reward;
	private int numAngles;

	public float getReward() {
		return reward;
	}

	public void setReward(float reward) {
		this.reward = reward;
	}

	public int getState() {
		return state;
	}

	public int getAction() {
		return action;
	}

	public StateActionReward(int state, int action, float reward, int numAngles) {
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.numAngles = numAngles;
	}

	public StateActionReward(int state, int action) {
		this.state = state;
		this.action = action;
		this.reward = 0;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof StateActionReward))
			return false;

		StateActionReward stateAction = (StateActionReward) o;

		return stateAction.state == state && stateAction.action == action;
	}

	public int hashCode() {
		return state * numAngles + action;
	}

}