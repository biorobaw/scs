package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
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
	boolean lastActionWasOptimal = false;

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
			float d = obtained - oldValue;
			
			//if last action was not optimal, do not update unless the new state is better than the old state
			delta.set( (d > 0 || lastActionWasOptimal) ? d : 0);
		} else {
			delta.set(0);
		}
			
		// Assumes Q is always the same size
		oldValue = value;
		validOldValue = true;
		lastActionWasOptimal = ((Bool0dPort)getInPort("isNextActionOptimal")).get();
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode() {
		validOldValue = false;
		lastActionWasOptimal = false;
	}

	/**
	 * Save the current Q state. Usually called at the beginning of an episode to update from the first move.
	 */
	public void saveValue() {
		oldValue = ((Float0dPort)getInPort("value")).get();
		validOldValue = true;
	}
}
