package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.Globals;
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
	Float0dPort maxActivation = new Float0dPort(this);
	boolean[] visited;
	
	public NextActiveModule(String name) {
		super(name);		
		this.addOutPort("nextActive", nextActivePort);
		this.addOutPort("active",activePort);
		this.addOutPort("maxActivation", maxActivation);

	}

	
	public void run() {
		int active = nextActivePort.get();
		activePort.set(active);
		Float2dSparsePort W = (Float2dSparsePort)getInPort("W");
		
//		System.out.println("active: "+active);
//		System.out.println("cols: "+W.getNCols());
		
		int maxId = 0;
		float maxVal = -Float.MAX_VALUE;
		if (!W.isRowEmpty(active))
			for (Entry e : W.getNonZeroRow(active).keySet())
				if (e.j != active && W.get(e.i,e.j) > maxVal){
					maxVal = W.get(e.i,e.j);
					maxId = e.j;
				}

		nextActivePort.set(maxId);
		if(visited[maxId]){
			Globals.getInstance().put("loopInReactivationPath", true);
			maxActivation.set(-Float.MAX_VALUE);
		} else {
			visited[maxId] = true;
			maxActivation.set(maxVal);
		}
		
		
	}
	
	public void setVisitedArray(boolean[] visited){
		this.visited = visited;
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
