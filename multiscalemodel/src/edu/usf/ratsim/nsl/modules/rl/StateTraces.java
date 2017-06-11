package edu.usf.ratsim.nsl.modules.rl;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;

public class StateTraces extends Module {

	private Float1dSparsePortMap traces;
	private float tracesDecay;
	private float minTrace;
	private Float1dSparsePort states;
	
	public StateTraces(String name, Float1dSparsePort states, float tracesDecay, float minTrace) {
		super(name);
		
		traces = new Float1dSparsePortMap(this, states.getSize(), states.getSparseness()); // TODO: make smarter sparseness 
		addOutPort("traces", traces);
		
		this.states  = states;
		this.tracesDecay = tracesDecay;
		this.minTrace = minTrace;
	}

	@Override
	public void run() {
		// Decrease all existing traces
		for (Integer s : traces.getNonZero().keySet()){
			float val = traces.get(s) * tracesDecay;
			// If too small, zero out
			val = val < minTrace ? 0 : val;
			traces.set(s, val);
		}
		
		// Include new traces as the current activation value
		for (Integer s : states.getNonZero().keySet())
			traces.set(s, states.get(s));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
