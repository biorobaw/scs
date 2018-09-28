package edu.usf.ratsim.model.morris_replay.submodules;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that computes error signal for V and Q policies according to the following formula:
 * errorV = r_t + gamma * V(s_t+1) - V(s_t) if action was optimal or errorV>0 
 *        = 0 otherwise
 * errorQ = r_t + gamma*V(s_t+1) - Q(s_t,a_t)
 * 
 */
public class VQErrorSignalModule extends Module {
	
	Float0dPort deltaV = new Float0dPort(this);
	Float0dPort deltaQ = new Float0dPort(this);
	
	float gamma; //discountFactor	
	
	boolean skipFirstUpdate;


	public VQErrorSignalModule(String name,float discountFactor) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("deltaV", deltaV);
		this.addOutPort("deltaQ", deltaQ);
		
		
		skipFirstUpdate = true;
	}

	
	public void run() {
		
		if (skipFirstUpdate){
			deltaV.set(0);
			deltaQ.set(0);
		} else {
		
			float r = ((Float0dPort)getInPort("reward")).get();
			float newStateValue  = ((Float0dPort)getInPort("newStateValue")).get();
			float oldStateValue  = ((Float0dPort)getInPort("oldStateValue")).get();
			Float1dPortArray oldActionValues = (Float1dPortArray)getInPort("oldActionValues");
			int action = ((Int0dPort)getInPort("action")).get();				
			
			float actionValue = oldActionValues.get(action);
			boolean wasActionOptimal = true;
			for(int i=0; wasActionOptimal && (i < oldActionValues.getSize()) ; i++){
				wasActionOptimal = actionValue >= oldActionValues.get(i);
			}
			
			
			// Maximum obtained is capped by food reward
			float obtained = r + gamma*newStateValue;

			//set deltaQ signel
			float dQ = obtained - actionValue;
			deltaQ.set(dQ);
			
			//set deltaV signal
			//if last action was not optimal, update only if received more reward than expected
			float dV = obtained - oldStateValue;
			deltaV.set( (dV > 0 || wasActionOptimal) ? dV : 0f);
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


}
