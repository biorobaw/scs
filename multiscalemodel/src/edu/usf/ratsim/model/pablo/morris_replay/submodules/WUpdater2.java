package edu.usf.ratsim.model.morris_replay.submodules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.twodimensional.Float2dSingleBlockMatrixPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class WUpdater2 extends Module {
	int numPlaceCells;
	float learningRate;
	Runnable run;

	private Float2dSingleBlockMatrixPort oldPCs;
	
//	private HashMap<Integer, Float> oldPCs;

	public WUpdater2(String name, int numPlaceCells, float learningRate) {
		super(name);

		this.numPlaceCells = numPlaceCells;
		this.learningRate = learningRate;
		this.run = new RunFirstTime();

	}

	public void run() {
		run.run();

	}

	public class RunFirstTime implements Runnable {

		@Override
		public void run() {
			oldPCs = ((Float2dSingleBlockMatrixPort) getInPort("PC")).copyPort();
			((Float2dSingleBlockMatrixPort) getInPort("PC")).copyData(oldPCs);;

			run = new RunGeneral();
		}

	}

	public class RunGeneral implements Runnable {

		@Override
		public void run() {
			Float2dSingleBlockMatrixPort pc = (Float2dSingleBlockMatrixPort) getInPort("PC");
			Float2dSparsePort wPort = (Float2dSparsePort) getInPort("wPort");

			int firstI = Math.min(pc.getStartRow(), oldPCs.getStartRow());
			int lastI =  Math.max(pc.getEndRow(), oldPCs.getEndRow());
			
			int firstJ = Math.min(pc.getStartCol(), oldPCs.getStartCol());
			int lastJ  = Math.max(pc.getEndCol(), oldPCs.getEndCol());

			for (int i1 = firstI; i1 <lastI;i1++)
			for (int j1 = firstJ; j1 <lastJ;j1++){
				
				int i = pc.getIndex(i1, j1);
				for (int i2 = firstI; i2 <lastI;i2++)
				for (int j2 = firstJ; j2 <lastJ;j2++){
					
					int j = pc.getIndex(i2, j2);
					float val = wPort.get(i, j);
					val += Math.atan((pc.get(i) + oldPCs.get(i)) / 2 * (pc.get(j) - oldPCs.get(j)));
					wPort.set(i, j, val);
				}
			}

			pc.copyData(oldPCs);;

//			System.out.println(wPort.getNonZero().keySet().size());
		}

	}

	public void newEpisode() {

		run = new RunFirstTime();
	}

	public void newTrial() {
		run = new RunFirstTime();
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
