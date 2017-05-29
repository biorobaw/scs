package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

public class UpdateQModule extends Module {
	
	
	private float alpha; //learning rate

	public UpdateQModule(String name,float alpha) {
		super(name);

		this.alpha = alpha;
	}

	
	public void run() {
		float nuDelta = alpha*((Float0dPort)getInPort("delta")).get();
		int action = ((Int0dPort)getInPort("action")).get();
		FloatMatrixPort Q = (FloatMatrixPort)getInPort("Q");
		Float1dSparsePort PCs = (Float1dSparsePort)getInPort("placeCells");
		
		if (nuDelta==0) return;
		for (int i : PCs.getNonZero().keySet())
			Q.set(i,action, Q.get(i,action) + nuDelta*PCs.get(i));
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
