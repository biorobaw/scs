package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to enable a random list of feeders
 * @author ludo
 *
 */
public class EnableRandomFeedersTriangle extends Task {

	private Random r;

	public EnableRandomFeedersTriangle(ElementWrapper params) {
		super(params);
		r = RandomSingleton.getInstance();
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}
	
	private void perform(Universe u){
		List<Integer> toEnable = new LinkedList<Integer>();
		List<Integer> feeders = new LinkedList<Integer>(u.getFeederNums());
		int numFeeders = feeders.size();
		
		int first = r.nextInt(numFeeders);
		toEnable.add(first);
		toEnable.add((first + 2) % numFeeders);
		toEnable.add((first + 4) % numFeeders);
		
		for (Integer f : toEnable)
			u.setEnableFeeder(f, true);
	}

}
