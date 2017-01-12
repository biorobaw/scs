package edu.usf.ratsim.nsl.modules.rl;

import java.util.HashMap;
import java.util.Map;

public class UpdateItem {

	public Map<Integer, Float> states;
	public int action;
	public float reward;
	public float valueEstBefore;
	public float valueEstAfter;

	public UpdateItem(Map<Integer, Float> states, int action, float reward, float valueEstBefore, float valueEstAfter) {
		this.states = new HashMap<Integer, Float>(states);
		this.action = action;
		this.reward = reward;
		this.valueEstBefore = valueEstBefore;
		this.valueEstAfter = valueEstAfter;
	}

}
