package edu.usf.experiment.condition;

import edu.usf.experiment.Episode;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodStopCond implements Condition {

	private int n;

	public FoundNFoodStopCond(ElementWrapper condParams) {
		this.n = condParams.getChildInt("n");
	}

	@Override
	public boolean holds(Episode e) {
		if (e.getSubject().hasEaten())
			n--;
		return n <= 0;
	}

}
