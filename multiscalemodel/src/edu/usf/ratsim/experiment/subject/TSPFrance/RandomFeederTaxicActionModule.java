package edu.usf.ratsim.experiment.subject.TSPFrance;

import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.List.Int1dPortList;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class RandomFeederTaxicActionModule extends Module {

	FeederTaxicAction action = new FeederTaxicAction(-1);
	ModelActionPort outport = new ModelActionPort(this, action);
	Runnable runFunction;
	Runnable runFirst = new RunFirstTime();
	Runnable runGeneral = new RunGeneral();
	
	int nextDestiny = -1;
	
	public RandomFeederTaxicActionModule(String name) {
		super(name);
		runFunction = runFirst;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		runFunction.run();
		
	}
	
	void makeNewChoice(int currentFeeder){
		Int1dPortList feeders = (Int1dPortList)getInPort("visibleFeeders");
		int cantFeeders = feeders.getSize();
		
		if(cantFeeders==0 || cantFeeders==1 && feeders.get(0)==currentFeeder){
			//I dont see any feeders or I'm standing in the only feeder I see
			//What shall be done?
			//System.out.println("CANT SET A NEW FEEDER TAXIC ACTION");
			action.setId(-1);
			
		}else{
			//There is at least one feeder (not including the one I'm standing if any)
			int selection = -1;
			do{
				selection = RandomSingleton.getInstance().nextInt(cantFeeders);
				//System.out.println("selection "+selection + "\t" + feeders.get(selection) + "\t" + currentFeeder);
			} while(feeders.get(selection) == currentFeeder);
			nextDestiny = feeders.get(selection);
			//System.out.println("NEXT DESTINY: "+nextDestiny);
			action.setId(nextDestiny);
		}
		
	}
	
	class RunFirstTime implements Runnable {
		//First time
		@Override
		public void run() {
			//System.out.println("First Tme");
			int currentFeeder = ((Int0dPort)getInPort("currentFeeder")).get();
			makeNewChoice(currentFeeder);
			runFunction = runGeneral;
			
		}
		
	}
	
	class RunGeneral implements Runnable {

		@Override
		public void run() {
			//System.out.println("General time");
			int currentFeeder = ((Int0dPort)getInPort("currentFeeder")).get();
			
			Int0dPort newSelectionPort = null;
			try{
				newSelectionPort = (Int0dPort)getInPort("newSelection"); //optional port, if 1 forces a new decision
			} catch(RuntimeException e){};
			 
			
			int newSelection = newSelectionPort == null ? 0 : newSelectionPort.get(); 	// check weather an external module forces to make a new decision
			boolean previousGoalCompleted = nextDestiny == currentFeeder;				// check if previous goal was achieved
			
			//check if the module must make a new selection
			//System.out.println("new selection/goal completed: " + newSelection + "\t"+previousGoalCompleted);
			if(newSelection == 1 || previousGoalCompleted) makeNewChoice(currentFeeder);
			
		}
		
		
	}

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}

}
