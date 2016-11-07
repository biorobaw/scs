package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class SarsaQDeltaError extends Module {
	
	Float0dPort delta = new Float0dPort(this);
	
	float gamma; //discountFactor	
	
	float[] oldQ;
	int oldAction;
	boolean initialized;

	public SarsaQDeltaError(String name,float discountFactor) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
		initialized = false;
	}
	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		int action = ((Int0dPort)getInPort("action")).get();
		
		if (initialized){
			delta.set(r + gamma*Q[action] - oldQ[oldAction]);
		} else {
			oldQ = new float[Q.length];
			System.arraycopy(Q, 0, oldQ, 0, Q.length);
			oldAction = action;
			initialized = true;
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void reset() {
		initialized = false;
	}
	
	
	
}
