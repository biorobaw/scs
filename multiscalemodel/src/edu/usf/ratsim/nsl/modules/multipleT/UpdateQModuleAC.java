package edu.usf.ratsim.nsl.modules.multipleT;

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

	private int numActions;

	private HashMap<Integer, Float> oldPCs;
	private float alpha;

	public UpdateQModuleAC(String name, int numActions, float alpha) {
		super(name);

		this.numActions = numActions;
		this.alpha = alpha;
	}

	public void run() {
		float alphaDelta = alpha * ((Float0dPort) getInPort("delta")).get();
		int action = ((Int0dPort) getInPort("action")).get();
		Float2dPort Q = (Float2dPort) getInPort("Q");

		if (oldPCs != null && alphaDelta != 0) {
			for (int i : oldPCs.keySet()) {
				Q.set(i, action, Q.get(i, action) + alphaDelta * oldPCs.get(i));
				// Update value
				Q.set(i, numActions, Q.get(i, numActions) + alphaDelta * oldPCs.get(i));
			}
		}

		Float1dSparsePort PCs = (Float1dSparsePort) getInPort("placeCells");
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
