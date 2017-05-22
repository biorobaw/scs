package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodStopCond implements Condition {

	private int n;
	private int cycles =0;
	private int extraCycles=0;

	public FoundNFoodStopCond(ElementWrapper condParams) {
		this.n = condParams.getChildInt("n");
		
		if(condParams.getChild("extraCycles")!=null)
			extraCycles = condParams.getChildInt("extraCycles");
		
		//System.out.println("extra cycles: " + extraCycles);
		//new java.util.Scanner(System.in).nextLine();
		
	}

	@Override
	public boolean holds(Episode e) {
		if(n<=0) cycles+=1;
		if (((FeederUniverse)e.getUniverse()).hasRobotEaten())
			n--;
		return n <= 0 && cycles >=extraCycles;
	}
	


}
