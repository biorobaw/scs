package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.HashSet;
import java.util.Set;

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
	
	boolean[] processedi;
	boolean[] processedj;
	int[] indexesi;
	int[] indexesj;
	int nextI=0;
	int nextJ=0;
	
	

	public PlaceCellTransitionMatrixUpdater(String name,int numPlaceCells,float learningRate) {
		super(name);

		this.numPlaceCells = numPlaceCells;
		this.learningRate = learningRate;
		this.run = new RunFirstTime();
		
		indexesi = new int[numPlaceCells];
		indexesj = new int[numPlaceCells];
		processedi = new boolean[numPlaceCells];
		processedj = new boolean[numPlaceCells];

	}

	public void run() {
		run.run();
		
	}
	
	public class RunFirstTime implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//do nothing in first run
			
			Float1dSparsePort pc = (Float1dSparsePort) getInPort("PC");
			for(int i : pc.getNonZero().keySet()){
				indexesj[nextJ]=i;
				nextJ++;
				processedj[i]=true;
				
			}
				
				
				
			
			run = new RunGeneral();
		}
	
	}
	
	public class RunGeneral implements Runnable{

		@Override
		public void run() {
			Float1dSparsePort pc = (Float1dSparsePort) getInPort("PC");
			Float1dSparsePort pcCopy = (Float1dSparsePort) getInPort("PCcopy");
			FloatMatrixPort wPort = (FloatMatrixPort) getInPort("wPort");
			
			for(int i : pc.getNonZero().keySet()){
				indexesi[nextI]=i;
				nextI++;
				processedi[i]=true;
				if(!processedj[i]){
					indexesj[nextJ]=i;
					nextJ++;
				}
				
			}
			
			//Set<Integer> keys = new HashSet<Integer>(pc.getNonZero().keySet());
			//keys.addAll(pcCopy.getNonZero().keySet());
			
			for (int i1=0;i1<nextJ;i1++)
				for (int j1=0; j1<nextJ;j1++)
				{
					int i=indexesj[i1];
					int j=indexesj[j1];
					float val = wPort.get(i, j);
					//val+= Math.atan(pc.get(i)*(pc.get(j)-pcCopy.get(j)));
					val+= Math.atan((pc.get(i)+pcCopy.get(i))/2*(pc.get(j)-pcCopy.get(j)));
					wPort.set(i, j, val);
				}
			
			
			//swap buffers
			int[] aux = indexesj;
			indexesj = indexesi;
			indexesi = aux;
			
			processedj = processedi;			
			nextJ=nextI;
			
			processedi = new boolean[numPlaceCells];
			nextI = 0;
			
			
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
