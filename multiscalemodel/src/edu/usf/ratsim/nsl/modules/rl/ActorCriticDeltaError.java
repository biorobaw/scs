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

	/**
	 * The index of the value in the q table
	 */
	private int valueIndex;

	private float[] oldQ;

	public ActorCriticDeltaError(String name,float discountFactor, int valueIndex) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
		this.valueIndex = valueIndex;
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		
		if (oldQ != null){
			delta.set(r + gamma*Q[valueIndex] - oldQ[valueIndex]);
		} else {
			delta.set(0);
			oldQ = new float[Q.length];
		}
			
		// Assumes Q is always the same size
		System.arraycopy(Q, 0, oldQ, 0, Q.length);
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode() {
		oldQ = null;
	}
}
