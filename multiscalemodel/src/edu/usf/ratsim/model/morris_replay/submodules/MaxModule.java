package edu.usf.ratsim.model.morris_replay.submodules;



import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * This module receives a list of floats and finds the maximum value and its index
 * 
 */
public class MaxModule extends Module {
	

	public Int0dPort maxArg = new Int0dPort(null);
	public Float0dPort maxVal = new Float0dPort(null);
	
	public MaxModule(String name){
		super(name);
		
		maxVal.set(Float.NEGATIVE_INFINITY);
		maxArg.set(0);
		
		addOutPort("maxArg", maxArg);
		addOutPort("maxVal",maxVal);
		
	}

	
	public void run() {
		Float1dPortArray vals = (Float1dPortArray)getInPort("values");
		float M = vals.get(0);
		int index = 0;
		for(int i=1;i<vals.getSize();i++) {
			if(vals.get(i) > M){
				M=vals.get(i);
				index=i;
			}
		}
		maxVal.set(M);
		maxArg.set(index);
		
	}

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		maxArg.set(0);
		maxVal.set(Float.NEGATIVE_INFINITY);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
