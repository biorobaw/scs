package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundAllFoodStopCond implements Condition {

	private boolean ateAllFood = false;
	private int cycles =0;
	private int extraCycles=0;

	public FoundAllFoodStopCond(ElementWrapper condParams) {		
		if(condParams.getChild("extraCycles")!=null)
			extraCycles = condParams.getChildInt("extraCycles");
		
		//System.out.println("extra cycles: " + extraCycles);
		//new java.util.Scanner(System.in).nextLine();
		
	}

	@Override
	public boolean holds(Episode e) {
		if(ateAllFood) cycles+=1;
		
		ateAllFood = true;
		for(Feeder f : e.getUniverse().getFeeders())
			if(f.hasFood()) {
				ateAllFood = false;
				break;
			} 
		
		return ateAllFood && cycles >=extraCycles;
	}
	


}
