//package edu.usf.ratsim.model.morris_replay.submodules;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import edu.usf.micronsl.module.Module;
//import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
//import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
//
///**
// * Module to generate random actions when the agent hasnt moved (just rotated)
// * for a while
// * 
// * @author biorob
// * 
// */
//public class WUpdater extends Module {
//	int numPlaceCells;
//	float learningRate;
//	Runnable run;
//
//	private HashMap<Integer, Float> oldPCs;
//
//	public WUpdater(String name, int numPlaceCells, float learningRate) {
//		super(name);
//
//		this.numPlaceCells = numPlaceCells;
//		this.learningRate = learningRate;
//		this.run = new RunFirstTime();
//
//	}
//
//	public void run() {
//		run.run();
//
//	}
//
//	public class RunFirstTime implements Runnable {
//
//		@Override
//		public void run() {
//			Float1dSparsePort pc = (Float1dSparsePort) getInPort("PC");
//			oldPCs = new HashMap<Integer, Float>(pc.getNonZero());
//
//			run = new RunGeneral();
//		}
//
//	}
//
//	public class RunGeneral implements Runnable {
//
//		@Override
//		public void run() {
//			Float1dSparsePort pc = (Float1dSparsePort) getInPort("PC");
//			Float2dSparsePort wPort = (Float2dSparsePort) getInPort("wPort");
//
//			Map<Integer, Float> pcNZ = pc.getNonZero();
//			Set<Integer> pcKS = pcNZ.keySet();
//			Set<Integer> oldPCKS = oldPCs.keySet();
//
//			for (Integer i : oldPCKS) {
//				for (Integer j : pcKS){
//					float val = wPort.get(i, j);
//					float oldPCI = oldPCs.containsKey(i) ? oldPCs.get(i) : 0;
//					float oldPCJ = oldPCs.containsKey(j) ? oldPCs.get(j) : 0;
//					// val+= Math.atan(pc.get(i)*(pc.get(j)-pcCopy.get(j)));
//					val += Math.atan((pc.get(i) + oldPCI) / 2 * (pc.get(j) - oldPCJ));
//					wPort.set(i, j, val);
//				}
//			}
//
//			oldPCs = new HashMap<Integer, Float>(pc.getNonZero());
//
////			System.out.println(wPort.getNonZero().keySet().size());
//		}
//
//	}
//
//	public void newEpisode() {
//
//		run = new RunFirstTime();
//	}
//
//	public void newTrial() {
//		run = new RunFirstTime();
//	}
//
//	@Override
//	public boolean usesRandom() {
//		return false;
//	}
//}
