package edu.usf.experiment.task;

import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
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
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		for (Feeder f : fu.getFeeders())
			fu.setEnableFeeder(f.getId(), true);
	}

}
