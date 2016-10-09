package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class PlaceCellTransitionMatrixUpdater extends Module {
	int numPlaceCells;
	float learningRate;
	Runnable run;
	

	public PlaceCellTransitionMatrixUpdater(String name,int numPlaceCells,float learningRate) {
		super(name);

		this.numPlaceCells = numPlaceCells;
		this.learningRate = learningRate;
		this.run = new RunFirstTime();
		
		

	}

	public void run() {
		
		
	}
	
	public class RunFirstTime implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//do nothing in first run
			run = new RunGeneral();
		}
	
	}
	
	public class RunGeneral implements Runnable{

		@Override
		public void run() {
			Float1dSparsePort pc = (Float1dSparsePort) getInPort("PC");
			Float1dSparsePort pcCopy = (Float1dSparsePort) getInPort("PCcopy");
			FloatMatrixPort wPort = (FloatMatrixPort) getInPort("wPort");
			
			for (int i : pc.getNonZero().keySet())
				for (int j =0;j<numPlaceCells;j++)
				{
					float val = wPort.get(i, j);
					val+= Math.atan(pc.get(i)*(pc.get(j)-pcCopy.get(j)));
					wPort.set(i, j, val);
				}
			
		}
		
	}
	
	public void newEpisode(){
		run = new RunFirstTime();
	}
	
	public void newTrial(){
		run = new RunFirstTime();
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
