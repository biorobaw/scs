package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.Globals;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class NextActiveModule extends Module {	

	Int0dPort nextActive = new Int0dPort(this);
	Float0dPort maxActivation = new Float0dPort(this);
	boolean[] visited;
	
	public NextActiveModule(String name) {
		super(name);		
		this.addOutPort("nextActive", nextActive);
		this.addOutPort("maxActivation", maxActivation);

	}

	
	public void run() {
		int active = ((Int0dPort)getInPort("active")).get();
		FloatMatrixPort W = (FloatMatrixPort)getInPort("W");
		
//		System.out.println("active: "+active);
//		System.out.println("cols: "+W.getNCols());
		
		int maxId = 0;
		float maxVal = -Float.MAX_VALUE;
		for (int j =0;j<W.getNCols();j++)
		{
			if(j!=active && W.get(active,j) > maxVal){
				maxVal = W.get(active,j);
				maxId = j;
			}

		}

		nextActive.set(maxId);
		if(visited[maxId]){
			Globals.getInstance().put("loopInReactivationPath", true);
		} else visited[maxId] = true;
		maxActivation.set(maxVal);
		
	}
	
	public void setVisitedArray(boolean[] visited){
		this.visited = visited;
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
