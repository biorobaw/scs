package edu.usf.ratsim.nsl.modules.rl;

public class UpdateItemSS {

	public int state;
	public int action;
	public float reward;
	public float valueEstBefore;
	public float valueEstAfter;

	public UpdateItemSS(int state, int action, float reward, float valueEstBefore, float valueEstAfter) {
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.valueEstBefore = valueEstBefore;
		this.valueEstAfter = valueEstAfter;
	}

}
