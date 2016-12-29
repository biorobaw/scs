package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
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
		for (Integer f : u.getFeederNums()){
			u.setEnableFeeder(f, true);
			u.setActiveFeeder(f, true);
			u.setPermanentFeeder(f, true);
		}
	}

}
