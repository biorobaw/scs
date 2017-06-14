package edu.usf.ratsim.nsl.modules.multipleT;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;

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
	public boolean loopEncountered = false;
	
	int numPC;
	
	public int active =0;
	
	LinkedList<PlaceCell> PCs;
	
	public NextActiveModule(String name, LinkedList<PlaceCell> PCs) {
		super(name);		
		this.addOutPort("nextActive", nextActive);
		this.addOutPort("maxActivation", maxActivation);
		
		this.numPC = PCs.size();
		this.PCs = PCs;
		visited = new boolean[numPC];
		
	}

	
	public void run() {
		Float2dSparsePort W = (Float2dSparsePort)getInPort("W");
		int active = nextActive.get();
		
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

		nextActive.set(maxId);
		if(visited[maxId]){
			maxActivation.set(-Float.MAX_VALUE);
			loopEncountered = true;
		} else {
			visited[maxId] = true;
			maxActivation.set(maxVal);
		}
		
		
	}
	
	
	public void newEpisode(){
		
		//activate random PC
		active = RandomSingleton.getInstance().nextInt(numPC);
		visited = new boolean[numPC]; // they get initialized to false
		visited[active] = true;
		loopEncountered = false;
		
		
		//place robot at center of actived PC
		Point3f initPos = PCs.get(active).getPreferredLocation();
		float theta = 0; //dont really care about head dir
		VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(initPos.x, initPos.y),theta);
		
		//set nextActive equal to active for consistency (since at each cycle active <= nextActive)
		nextActive.set(active);
		
		
	
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
