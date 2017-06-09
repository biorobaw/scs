package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.io.IOException;

import edu.usf.experiment.robot.componentInterfaces.FeederVisibilityInterface;
import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.List.Int1dPortList;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class RandomOrClosestFeederTaxicActionModule extends Module {

	FeederTaxicAction action = new FeederTaxicAction(-1);
	ModelActionPort outport = new ModelActionPort(this, action);
	Runnable runFunction;
	Runnable runFirst = new RunFirstTime();
	Runnable runGeneral = new RunGeneral();
	
	int nextDestiny = -1; // keeps track if already executing a feeder taxic action
	
	
	float moveToClosestProbability = 0;
	
	Subject sub;
	
	
//	int nextDecision[] = new int[] {3,18,5,21,9,1};
//	int next = 0;
	
	
	//MUST RECEIVE PORTS: feederSet,  currentFeeder
	public RandomOrClosestFeederTaxicActionModule(String name,Subject _sub,float _moveToClosestProbability) {
		super(name);
		runFunction = runFirst;
		moveToClosestProbability = _moveToClosestProbability;
		sub = _sub;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		runFunction.run();
		
	}
	
	void makeNewChoice(){
		
		
		Int1dPortList feeders = (Int1dPortList)getInPort("feederSet"); 
		int cantFeeders = feeders.getSize();
		
//		nextDestiny = nextDecision[next++];
//		action.setId(nextDestiny);
		
		if(cantFeeders==0 ){
			//I dont see any feeders or I'm standing in the only feeder I see
			//What shall be done? ERROR FOR NOW
			//System.out.println("CANT SET A NEW FEEDER TAXIC ACTION");
			action.setId(-1);
			
			
		}else if(RandomSingleton.getInstance().nextFloat() < moveToClosestProbability){
			
			//choose closest feeder in set
			System.out.println("CLOSEST");
			System.out.println(feeders.data);
			nextDestiny = (  (FeederVisibilityInterface)sub.getRobot()).getClosestFeeder(feeders.data);
			action.setId( nextDestiny );
			
			
		} else {

			System.out.println("RANDOM");
			nextDestiny = feeders.get(RandomSingleton.getInstance().nextInt(cantFeeders));
			//System.out.println("NEXT DESTINY: "+nextDestiny);
			action.setId(nextDestiny);
		}
		
		System.out.println("NEXT DESTINY "+ nextDestiny);
		
		
//		try {
//	        System.in.read();
//	        while(System.in.available()>0) System.in.read();
//	    } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }
		
		
		
		
	}
	
	class RunFirstTime implements Runnable {
		//First time always make a new selection
		@Override
		public void run() {
			//System.out.println("First Tme");
			makeNewChoice();
			runFunction = runGeneral;
			
		}
		
	}
	
	class RunGeneral implements Runnable {

		@Override
		public void run() {
//			//System.out.println("General time");
//			int currentFeeder = ((Int0dPort)getInPort("currentFeeder")).get();
//			
//			boolean previousGoalCompleted = nextDestiny == currentFeeder;				// check if previous goal was achieved
//			
//			//Check if external module forces to make new decision:
//			int newSelection = 0;
//			try{
//				Int0dPort newSelectionPort = ((Int0dPort)getInPort("newSelection")); //optional port, if 1 forces a new decision
//				newSelection = newSelectionPort.get();
//			} catch(RuntimeException e){};
//			 			
//			
//			//check if the module must make a new selection
//			if(newSelection == 1 || previousGoalCompleted) makeNewChoice();
			
			
			if(((Bool0dPort)getInPort("newSelection")).get()) makeNewChoice();
			
		}
		
		
	}
	
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		runFunction = runFirst;
		super.newEpisode();
	}

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}

}
