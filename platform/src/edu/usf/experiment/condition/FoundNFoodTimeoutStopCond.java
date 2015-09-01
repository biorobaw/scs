package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodTimeoutStopCond implements Condition {

	private int stepsSinceLastAte;
	private int timeout;
	private int n;
	private int toGo;

	public FoundNFoodTimeoutStopCond(ElementWrapper condParams) {
		n = condParams.getChildInt("n");
		toGo = n;
		timeout = condParams.getChildInt("timeout");
		stepsSinceLastAte = 0;
	}

	@Override
	public boolean holds(Episode episode) {
		Subject sub = episode.getSubject();

		if (sub.hasEaten()) {
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
