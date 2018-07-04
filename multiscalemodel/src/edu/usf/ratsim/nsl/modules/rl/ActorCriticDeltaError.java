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
	
	boolean skipFirstUpdate;

	private float foodReward;

	public ActorCriticDeltaError(String name,float discountFactor, float foodReward) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
		
		this.foodReward = foodReward;
		
		skipFirstUpdate = false;
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float newStateValue = ((Float0dPort)getInPort("newStateValue")).get();
		float oldStateValue = ((Float0dPort)getInPort("oldStateValue")).get();
		boolean wasActionOptimal = ((Bool0dPort)getInPort("wasActionOptimal")).get();
		
		
		if (skipFirstUpdate){
			delta.set(0);
		} else {
			// Maximum obtained is capped by food reward
			float obtained = Math.min(r + gamma*newStateValue, foodReward);
			float d = obtained - oldStateValue;
			
			//if last action was not optimal, do not update unless the new state is better than the old state
			delta.set( (d > 0 || wasActionOptimal) ? d : 0f);
		}
			
		// Assumes Q is always the same size
		skipFirstUpdate = false;
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode() {
		skipFirstUpdate = true;
	}

	/**
	 * why??? Save the current Q state. Usually called at the beginning of an episode to update from the first move.
	 */
//	public void saveValue() {
//		skipFirstUpdate = true;
//	}
}
