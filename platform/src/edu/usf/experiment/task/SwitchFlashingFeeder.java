package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class SwitchFlashingFeeder extends Task {

	private int feeder;
	private Random random;

	public SwitchFlashingFeeder(ElementWrapper params) {
		super(params);
		random = RandomSingleton.getInstance();
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getSubject(), experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getSubject(),  trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getSubject(),  episode.getUniverse());
	}
	
	private void perform(Subject s, Universe u) {
		if(s.hasEaten()){
			int feeder = u.getFeedingFeeder();
			
			// Deactivate the feeding one
			u.setActiveFeeder(feeder, false);
			u.setFlashingFeeder(feeder, false);
			
			List<Integer> enabled = u.getEnabledFeeders();
			enabled.remove(new Integer(feeder));
			for (Integer f : enabled){
//				u.setActiveFeeder(f, true);
				u.setFlashingFeeder(f, false);
			}
			
			// Pick an active one and flash
			int toFlash = enabled.get(random.nextInt(enabled.size()));
			u.setActiveFeeder(toFlash, true);
			u.setFlashingFeeder(toFlash, true);
		}
	}


}
