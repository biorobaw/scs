package edu.usf.experiment.condition;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodStopCond extends Condition {

	private int n;
	private int remaining; 
	private int cycles =0;
	private int extraCycles=0;

	public FoundNFoodStopCond(ElementWrapper condParams) {
		this.n = condParams.getChildInt("n");
		remaining = n;
		
		if(condParams.getChild("extraCycles")!=null)
			extraCycles = condParams.getChildInt("extraCycles");
		
		//System.out.println("extra cycles: " + extraCycles);
		//new java.util.Scanner(System.in).nextLine();
		
	}

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		cycles = 0;
		remaining = n; 
	}
	
	@Override
	public boolean holds() {
		if(remaining<=0) cycles+=1;
		if (((FeederUniverse)Universe.getUniverse()).hasRobotEaten())
			remaining--;
		return remaining <= 0 && cycles >=extraCycles;
	}
	


}
