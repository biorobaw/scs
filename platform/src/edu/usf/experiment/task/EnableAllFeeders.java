package edu.usf.experiment.task;

import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to enable a random list of feeders
 * @author ludo
 *
 */
public class EnableAllFeeders extends Task {

	private int numFeeders;
	private Random r;

	public EnableAllFeeders(ElementWrapper params) {
		super(params);
	}

	public void perform(Universe u, Subject s){
		for (Integer f : u.getFeederNums())
			u.setEnableFeeder(f, true);
	}

}
