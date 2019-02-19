package edu.usf.ratsim.model.pablo.morris_replay.submodules;

import java.util.HashMap;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be
 * performed It assumes actions are allocentric, distributed uniformly in the
 * range [0,2pi]
 * 
 * @author biorob
 * 
 */
public class UpdateQModuleAC2 extends Module {


	private Float2dSingleBlockMatrixPort oldPCs;
	private float alpha;
	private boolean skip;

	public UpdateQModuleAC2(String name, float alpha) {
		super(name);

		this.alpha = alpha;
	}

	public void run() {
		float alphaDeltaV = alpha * ((Float0dPort) getInPort("deltaV")).get();
		float alphaDeltaQ = alpha * ((Float0dPort) getInPort("deltaQ")).get();
		int action = ((Int0dPort) getInPort("action")).get();
		Float2dPort Q = (Float2dPort) getInPort("Q");
		Float2dPort V = (Float2dPort) getInPort("V");

		if (!skip && (alphaDeltaV != 0 || alphaDeltaQ != 0 )) {
			
			
			for(int i=0;i<oldPCs.getBlockRows();i++)
				for(int j=0;j<oldPCs.getBlockRows();j++){
					float pc = oldPCs.getBlock(i,j);
					int id = oldPCs.getBlockIndex(i, j);
					Q.set(id, action, Q.get(id, action) + alphaDeltaQ * pc);
					V.set(id, 0, V.get(id, 0) + alphaDeltaV * pc);
				}
			

		}
		
		skip  = false;
		((Float2dSingleBlockMatrixPort) getInPort("pcs")).copyDataTo(oldPCs);		

	}

	@Override
	public boolean usesRandom() {
		return false;
	}
	
	@Override
	public void newEpisode() {
		skip = true;
		Float2dSingleBlockMatrixPort pcs = (Float2dSingleBlockMatrixPort) getInPort("pcs");
		oldPCs = pcs.copyPort();
	}

	//what is this function for?
//	public void savePCs() {
//		Float1dSparsePort actionPCs = (Float1dSparsePort) getInPort("actionPlaceCells");
//		oldActionPCs = new HashMap<Integer, Float>(actionPCs.getNonZero());
//		Float1dSparsePort valuePCs = (Float1dSparsePort) getInPort("valuePlaceCells");
//		oldValuePCs = new HashMap<Integer, Float>(valuePCs.getNonZero());
//	}
}
