package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

/**
 * Module that computes the delta for actor critic modules
 * @author biorob
 * 
 */
public class ActorCriticDeltaError extends Module {
	
	Float0dPort delta = new Float0dPort(this);
	
	float gamma; //discountFactor	
	
	float oldValue;
	boolean validOldValue;

	private float foodReward;

	public ActorCriticDeltaError(String name,float discountFactor, float foodReward) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
		
		this.foodReward = foodReward;
		
		oldValue = 0;
		validOldValue = false;
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float value = ((Float0dPort)getInPort("value")).get();
		
		if (validOldValue){
			// Maximum obtained is capped by food reward
			float obtained = Math.min(r + gamma*value, foodReward);
			delta.set(obtained - oldValue);
		} else {
			delta.set(0);
		}
			
		// Assumes Q is always the same size
		oldValue = value;
		validOldValue = true;
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode() {
		validOldValue = false;
	}

	/**
	 * Save the current Q state. Usually called at the beginning of an episode to update from the first move.
	 */
	public void saveValue() {
		oldValue = ((Float0dPort)getInPort("value")).get();
		validOldValue = true;
	}
}
