package edu.usf.experiment.condition;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodTimeoutStopCond extends Condition {

	private int stepsSinceLastAte;
	private int timeout;
	private int originalN;
	private int n;
	private int toGo;

	public FoundNFoodTimeoutStopCond(ElementWrapper condParams) {
		n = condParams.getChildInt("n");
		toGo = n;
		originalN = n;
		timeout = condParams.getChildInt("timeout");
		stepsSinceLastAte = 0;
	}

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		toGo = n;
		stepsSinceLastAte = 0;
	}
	
	@Override
	public boolean holds() {
		FeederUniverse fu = (FeederUniverse) Universe.getUniverse();
			
		if (fu.hasRobotEaten()) {
			stepsSinceLastAte = 0;
			toGo--;
		} else
			stepsSinceLastAte++;

		if (stepsSinceLastAte >= timeout) {
			if (Debug.printFoundNNoMistakes)
				System.out.println("Reseting count of feeders");
			toGo = n;
			stepsSinceLastAte = 0;
		}

		if (Debug.printFoundNNoMistakes)
			System.out.println("Feeders to go " + toGo);

		return toGo <= 0;
	}


	
}
