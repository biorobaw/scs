package edu.usf.ratsim.experiment.subject.pablo.morris_replay;

import java.util.LinkedList;

import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;

class SimpleReplayModel {
	 
		Float2dSparsePort W = null;
		int numPCs = -1;
		
		
		public SimpleReplayModel(int _numPCs, Float2dSparsePort _W){
			numPCs = _numPCs;
			W = _W;
			
		}
		
		
		
		LinkedList<Integer> doSimpleReplay(int maxSynapses, float replayThreshold){
			
			LinkedList<Integer> replayPath = new LinkedList<Integer>();
			int active = RandomSingleton.getInstance().nextInt(numPCs);
			boolean visited[] = new boolean[numPCs];
			for(int i=0;i<numPCs;i++) visited[i]=false;
			replayPath.add(active);
			
			boolean done = false;
			while(maxSynapses-- > 0 && !done){
				
				int maxId = -1;
				float maxVal = replayThreshold;
								
				if (!W.isRowEmpty(active))
					for (Entry e : W.getNonZeroRow(active).keySet())
						if (e.j != active && W.get(e.i,e.j) > maxVal){
							maxVal = W.get(e.i,e.j);
							maxId = e.j;
					}

				if(maxVal > replayThreshold){
					
					active = maxId;
					replayPath.add(active);
					
					if(visited[active]) done = true;
					else visited[active] = true;
					
				} else done =true;
				
			}
			
			return replayPath;
			
			
		}
		
		
		
		
		
			
	 
	 
 }


	