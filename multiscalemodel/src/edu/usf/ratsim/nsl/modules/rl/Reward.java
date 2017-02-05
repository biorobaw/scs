package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

public class Reward extends Module {

	private Float0dPort reward;
	private float nonFoodReward;
	private float foodReward;

	public Reward(String name, float foodReward,
			float nonFoodReward) {
		super(name);
		reward =  new Float0dPort(this);
		addOutPort("reward",reward);

		this.foodReward = foodReward;
		this.nonFoodReward = nonFoodReward;
	}

	public void run() {
		Bool0dPort rewardingEvent = (Bool0dPort) getInPort("rewardingEvent");
		if (rewardingEvent.get()) {
			reward.set(foodReward);
		} else {
			reward.set(nonFoodReward);
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
