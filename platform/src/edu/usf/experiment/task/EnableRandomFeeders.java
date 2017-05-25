package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to enable a random list of feeders
 * @author ludo
 *
 */
public class EnableRandomFeeders extends Task {

	private int numFeeders;
	private Random r;

	public EnableRandomFeeders(ElementWrapper params) {
		super(params);
		numFeeders = params.getChildInt("numFeeders");
		r = RandomSingleton.getInstance();
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		List<Integer> toEnable = new LinkedList<Integer>();
		List<Integer> feeders = new LinkedList<Integer>(FeederUniverseUtilities.getFeederNums(fu.getFeeders()));
		for (int i = 0; i < numFeeders; i++){
			int index = r.nextInt(feeders.size());
			toEnable.add(feeders.get(index));
			feeders.remove(index);
		}
			
		for (Integer f : toEnable)
			fu.setEnableFeeder(f, true);
	}

}
