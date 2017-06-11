package edu.usf.ratsim.nsl.modules.rl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;

/**
 * 
 * @author biorob
 * 
 */
public class UpdateQModuleACTraces extends Module {


	private HashMap<Integer, Float> oldActionPCs;
	private HashMap<Integer, Float> oldValuePCs;
	
	/**
	 * The eligibility traces for states
	 */
	private Map<Integer, Float> valueTraces;

	/**
	 * Eligibility traces for action-state combinations
	 */
	private Map<Entry, Float> actionTraces;
	
	private float alpha;
	private float tracesDecay;
	private float minTrace;

	public UpdateQModuleACTraces(String name, float alpha, float tracesDecay, float minTrace) {
		super(name);

		this.alpha = alpha;
		this.tracesDecay = tracesDecay;
		this.minTrace = minTrace;
		
		valueTraces = new HashMap<Integer, Float>();
		actionTraces = new HashMap<Entry, Float>();
	}

	public void run() {
		float alphaDelta = alpha * ((Float0dPort) getInPort("delta")).get();
		int action = ((Int0dPort) getInPort("action")).get();
		Float2dPort Q = (Float2dPort) getInPort("Q");
		Float2dPort V = (Float2dPort) getInPort("V");
		
		if (oldActionPCs != null && alphaDelta != 0) {
			// Add new traces
			for (Integer s : oldActionPCs.keySet()){
				float activation = oldActionPCs.get(s);
				Entry sa = new Entry(s, action);
				if (!actionTraces.containsKey(sa))
					actionTraces.put(sa, activation);
				float val = actionTraces.get(sa);
				actionTraces.put(sa, Math.max(activation, val));
			}
			// i == s, j == action
			for (Entry sa : actionTraces.keySet()) {
				Q.set(sa.i, sa.j, Q.get(sa.i, sa.j) + alphaDelta * actionTraces.get(sa));
			}
		}
		
		if (oldValuePCs != null && alphaDelta != 0) {
			for (Integer s : oldValuePCs.keySet()){
				float activation = oldValuePCs.get(s);
				if (!valueTraces.containsKey(s))
					valueTraces.put(s, activation);
				float val = valueTraces.get(s);
				valueTraces.put(s, Math.max(activation, val));
			}		
			for (int i : valueTraces.keySet()) {
				V.set(i, 0, V.get(i, 0) + alphaDelta * valueTraces.get(i));
			}
		}
		
		// Decrement all existing traces
		Set<Entry> toRemove = new HashSet<Entry>();
		for (Entry sa : actionTraces.keySet()){
			float val = actionTraces.get(sa);
			val *= tracesDecay;
			if (val < minTrace)
				toRemove.add(sa);
			else
				actionTraces.put(sa, val);
		}
		for (Entry sa : toRemove)
			actionTraces.remove(sa);
		
		Set<Integer> valueTraceRemove = new HashSet<Integer>();
		for (Integer s : valueTraces.keySet()){
			float val = valueTraces.get(s);
			val *= tracesDecay;
			val = val < minTrace ? 0 : val;
			if (val < minTrace)
				valueTraceRemove.add(s);
			else
				valueTraces.put(s, val);
		}
		for (Integer s : valueTraceRemove)
			valueTraces.remove(s);
	
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
		actionTraces.clear();
		valueTraces.clear();
	}

	public void savePCs() {
		Float1dSparsePort actionPCs = (Float1dSparsePort) getInPort("actionPlaceCells");
		oldActionPCs = new HashMap<Integer, Float>(actionPCs.getNonZero());
		Float1dSparsePort valuePCs = (Float1dSparsePort) getInPort("valuePlaceCells");
		oldValuePCs = new HashMap<Integer, Float>(valuePCs.getNonZero());
	}
}
