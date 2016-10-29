package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

public class CumulativeReward extends Module {

	private Float0dPort reward;
	private float accReward;
	
	public CumulativeReward(String name) {
		super(name);
		reward =  new Float0dPort(this);
		addOutPort("reward",reward);

		accReward = 0;
	}

	public void run() {
		Bool0dPort rewardingEvent = (Bool0dPort) getInPort("rewardingEvent");
		Float0dPort instantReward = (Float0dPort) getInPort("instantReward");
		Bool0dPort subTriedToEat = (Bool0dPort) getInPort("subTriedToEat");
		
		accReward += instantReward.get();
		reward.set(accReward);
		
		System.out.println("Cumm reward " + accReward);

		if (subTriedToEat.get()) {
			accReward = 0;
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
