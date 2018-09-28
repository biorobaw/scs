package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class NextActiveModule extends Module {	

	Int0dPort nextActivePort = new Int0dPort(this);
	Int0dPort activePort	 = new Int0dPort(this);
//	Float0dPort maxActivation = new Float0dPort(this);
	float propagationThreshold = 0;
	boolean[] visited;
	
	public boolean propagate;
	
	public NextActiveModule(String name,float threshold) {
		super(name);		
		this.addOutPort("nextActive", nextActivePort);
		this.addOutPort("active",activePort);
//		this.addOutPort("maxActivation", maxActivation);
		this.propagationThreshold = Math.max(threshold,0);

	}

	
	public void run() {
		int active = nextActivePort.get();
		activePort.set(active);
		Float2dSparsePort W = (Float2dSparsePort)getInPort("W");
		
//		System.out.println("active: "+active);
//		System.out.println("cols: "+W.getNCols());
		
//		maxConnectionStrategy(active,W);
		canonicalDistribuitionStrategy(active,W);
		
	}
	
	
	public void maxConnectionStrategy(int active, Float2dSparsePort W){
		
		int maxId = 0;
		float maxVal = -Float.MAX_VALUE;
		if (!W.isRowEmpty(active))
			for (Entry e : W.getNonZeroRow(active).keySet())
				if (e.j != active && W.get(e.i,e.j) > maxVal){
					maxVal = W.get(e.i,e.j);
					maxId = e.j;
				}
		
//		maxActivation.set(maxVal);
		nextActivePort.set(maxId);
		
//		nextActivePort.set(maxId);
		
		propagate = propagationThreshold < maxVal && !visited[maxId];
		visited[maxId] = true;
		
	}
	
	public void canonicalDistribuitionStrategy(int active, Float2dSparsePort W){
		
		
		float sum = 0;
		if (!W.isRowEmpty(active))
			for (Entry e : W.getNonZeroRow(active).keySet())
				if (e.j != active && W.get(e.i,e.j) > propagationThreshold){
					sum+=W.get(e.i,e.j);
				}
		
		if(sum>0){
			float r = sum*RandomSingleton.getInstance().nextFloat();
			
			float partial = 0;
			int id = 0;
			for (Entry e : W.getNonZeroRow(active).keySet())
				if (e.j != active && W.get(e.i,e.j) > propagationThreshold){
					partial+=W.get(e.i,e.j);
					if(partial >= r){
						id = e.j;
						break;
					}
				}
			
			propagate = !visited[id];
			visited[id] = true;
			nextActivePort.set(id);
			
		}else{
			nextActivePort.set(0);
			propagate = false;
			
		}
		
		
		
		
	}
	
	
	public void setVisitedArray(boolean[] visited){
		this.visited = visited;
	}


	@Override
	public boolean usesRandom() {
		return true;
	}
	
	@Override
	public void newEpisode(){
		propagate = false;
	}
}
