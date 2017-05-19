package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to activate all enabled feeders
 * @author ludo
 *
 */
public class SetAllFeedersPermanentFood extends Task {

	public SetAllFeedersPermanentFood(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		for (Integer f : fu.getFeederNums()){
			fu.setEnableFeeder(f, true);
			fu.setActiveFeeder(f, true);
			fu.setPermanentFeeder(f, true);
		}
	}

}
