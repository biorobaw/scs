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
public class EnableRandomFeeders extends Task {

	private int numFeeders;
	private Random r;

	public EnableRandomFeeders(ElementWrapper params) {
		super(params);
		numFeeders = params.getChildInt("numFeeders");
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
		for (int i = 0; i < numFeeders; i++){
			int index = r.nextInt(feeders.size());
			toEnable.add(feeders.get(index));
			feeders.remove(index);
		}
			
		for (Integer f : toEnable)
			u.setEnableFeeder(f, true);
	}

}
