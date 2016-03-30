package edu.usf.ratsim.nsl.modules.qlearning;

import edu.usf.micronsl.Bool1dPort;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.Module;

public class Reward extends Module {

	private float[] reward;
	private float nonFoodReward;
	private float foodReward;

	public Reward(String name, float foodReward,
			float nonFoodReward) {
		super(name);
		reward = new float[1];
		addOutPort("reward", new Float1dPortArray(this, reward));

		this.foodReward = foodReward;
		this.nonFoodReward = nonFoodReward;
	}

	public void run() {
		Bool1dPort subAte = (Bool1dPort) getInPort("subAte");
		if (subAte.get()) {
			reward[0] = foodReward;
//			System.out.println("Rewarding");
		} else {
			reward[0] = nonFoodReward;
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
