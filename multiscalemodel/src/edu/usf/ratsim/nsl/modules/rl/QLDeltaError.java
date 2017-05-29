package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that computes the delta for actor critic modules
 * @author biorob
 * 
 */
public class QLDeltaError extends Module {
	
	float gamma; //discountFactor	

	private float[] oldQ;

	private Float0dPort delta;

	public QLDeltaError(String name,float discountFactor) {
		super(name);
		gamma = discountFactor;
		delta = new Float0dPort(this);
		this.addOutPort("delta", delta);
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		int action = ((Int0dPort)getInPort("action")).get();
		
		float maxQ = -Float.MAX_VALUE;
		for (int i = 0; i < Q.length; i++)
			if (Q[i] > maxQ)
				maxQ = Q[i];
		
		if (oldQ != null){
			delta.set(r + gamma*maxQ - oldQ[action]);
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


	/**
	 * Save the current Q state. Usually called at the beginning of an episode to update from the first move.
	 */
	public void saveQ() {
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		oldQ = new float[Q.length];
		System.arraycopy(Q, 0, oldQ, 0, Q.length);
	}
}
