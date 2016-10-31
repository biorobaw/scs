package edu.usf.ratsim.experiment.subject.multipleT;

import edu.usf.experiment.Episode;
import edu.usf.experiment.condition.Condition;
import edu.usf.experiment.utils.ElementWrapper;

public class PCActivationBelowThresholdCondition implements Condition {

	int chance = 1;

	public PCActivationBelowThresholdCondition(ElementWrapper condParams) {

	}

	@Override
	public boolean holds(Episode episode) {
		MultipleTSubject sub =(MultipleTSubject) episode.getSubject();

		if(chance==0) return true;
		if(!sub.replayAboveThreshold())
			if(sub.hasEaten()) chance--;
			else return true;
		return false;
		
	}


	
}
