package edu.usf.ratsim.nsl.modules.rl;

import java.util.HashMap;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

public class UpdateQModule extends Module {

	private float alpha; // learning rate
	private HashMap<Integer, Float> oldPCs;

	public UpdateQModule(String name, float alpha) {
		super(name);

		this.alpha = alpha;

		oldPCs = null;
	}

	public void run() {
		float alphaDelta = alpha*((Float0dPort)getInPort("delta")).get();
		int action = ((Int0dPort)getInPort("action")).get();
		FloatMatrixPort Q = (FloatMatrixPort)getInPort("Q");
		Float1dSparsePort PCs = (Float1dSparsePort)getInPort("placeCells");
		
		if (oldPCs != null && alphaDelta != 0){
			for (int i : oldPCs.keySet()){
				Q.set(i,action, Q.get(i,action) + alphaDelta*oldPCs.get(i));
			}
		} 
			
		oldPCs = new HashMap<Integer, Float>(PCs.getNonZero());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void newEpisode() {
		oldPCs = null;
	}

	public void savePCs() {
		Float1dSparsePort PCs = (Float1dSparsePort) getInPort("placeCells");
		oldPCs = new HashMap<Integer, Float>(PCs.getNonZero());
	}
}
