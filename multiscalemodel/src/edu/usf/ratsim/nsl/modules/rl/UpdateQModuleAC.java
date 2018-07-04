package edu.usf.ratsim.nsl.modules.rl;

import java.util.HashMap;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be
 * performed It assumes actions are allocentric, distributed uniformly in the
 * range [0,2pi]
 * 
 * @author biorob
 * 
 */
public class UpdateQModuleAC extends Module {


	private HashMap<Integer, Float> oldActionPCs;
	private HashMap<Integer, Float> oldValuePCs;
	private float alpha;

	public UpdateQModuleAC(String name, float alpha) {
		super(name);

		this.alpha = alpha;
	}

	public void run() {
		float alphaDelta = alpha * ((Float0dPort) getInPort("delta")).get();
		int action = ((Int0dPort) getInPort("action")).get();
		Float2dPort Q = (Float2dPort) getInPort("Q");
		Float2dPort V = (Float2dPort) getInPort("V");

		if (oldActionPCs != null && alphaDelta != 0) {
			for (int i : oldActionPCs.keySet()) {
				Q.set(i, action, Q.get(i, action) + alphaDelta * oldActionPCs.get(i));
			}
		}
		
		if (oldValuePCs != null && alphaDelta != 0) {
			for (int i : oldValuePCs.keySet()) {
				V.set(i, 0, V.get(i, 0) + alphaDelta * oldValuePCs.get(i));
			}
		}
		
		Float1dSparsePort actionPCs = (Float1dSparsePort) getInPort("actionPlaceCells");
		oldActionPCs = new HashMap<Integer, Float>(actionPCs.getNonZero());
		Float1dSparsePort valuePCs = (Float1dSparsePort) getInPort("valuePlaceCells");
		oldValuePCs = new HashMap<Integer, Float>(valuePCs.getNonZero());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
	
	@Override
	public void newEpisode() {
		oldActionPCs = null;
		oldValuePCs = null;
	}

	//what is this function for?
//	public void savePCs() {
//		Float1dSparsePort actionPCs = (Float1dSparsePort) getInPort("actionPlaceCells");
//		oldActionPCs = new HashMap<Integer, Float>(actionPCs.getNonZero());
//		Float1dSparsePort valuePCs = (Float1dSparsePort) getInPort("valuePlaceCells");
//		oldValuePCs = new HashMap<Integer, Float>(valuePCs.getNonZero());
//	}
}
