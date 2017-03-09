package edu.usf.ratsim.experiment.subject.TSPFrance;

import TRN4JAVA.*;
import java.util.LinkedList;
import java.util.Map;

import edu.usf.experiment.robot.RobotAction;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class ReservoirActionSelectionModule extends Module {

	//FeederTaxicAction action = new FeederTaxicAction(-1);
	RobotAction action = new RobotAction("reservoirAction");
	//action = new DifferentialNavigationAction(leftSpeed,RightSpeed)
	ModelActionPort outport = new ModelActionPort(this, action);
	Reservoir reservoir;
	float[] reservoirPrediction;
	
	
	Runnable runFunction;
	Runnable runFirst = new RunFirstTime();
	Runnable runGeneral = new RunGeneral();
	
	LinkedList<Map<Integer,Float>> pcHistory = new LinkedList<Map<Integer,Float>>(); 
	
	int iteration  = 0;
	int memorySize = 5;
	
	public ReservoirActionSelectionModule(String name, Reservoir reservoir) {
		super(name);
		runFunction = runFirst;
		this.reservoir = reservoir;
		reservoir.finishInitialization(new ReceiveLoop());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		runFunction.run();
		
	}
	
	
	
	class RunFirstTime implements Runnable {
		//First time
		@Override
		public void run() {
			//System.out.println("First Tme");
			Float1dSparsePortMap pcs = (Float1dSparsePortMap)getInPort("placeCells");
			pcHistory.add(pcs.getNonZero());
			
			
			reservoir.transmit(pcs.getData());
			
			//wait for receive
						
			if(iteration < memorySize) iteration++;
			else runFunction = runGeneral;
			
		}
		
	}
	
	class RunGeneral implements Runnable {

		@Override
		public void run() {
			//System.out.println("General time");
			Float1dSparsePortMap pcs = (Float1dSparsePortMap)getInPort("placeCells");
			pcHistory.add(pcs.getNonZero());
			pcHistory.removeFirst();
			
			reservoir.transmit(pcs.getData());
			//wait for receive
			
			//convert prediction to position

			
		}
		
		
	}

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void newEpisode(){
		iteration = 0;
		pcHistory.clear();
		runFunction = runFirst;
		
	}
	
	
	class ReceiveLoop extends TRN4JAVA.Loop {

		@Override
		public void prediction(final float[] prediction) {
			// TODO Auto-generated method stub
			reservoirPrediction = prediction;
		}
		
		
	}

}
