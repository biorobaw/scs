package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 *
 */
public class SwitchFeeder extends Task {

	private Random random;

	public SwitchFeeder(ElementWrapper params) {
		super(params);
		random = RandomSingleton.getInstance();
	}

	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		if (s.hasEaten()) {
			int eatenFeeder = fu.getFeedingFeeder();

			// Deactivate the feeding one
			fu.setActiveFeeder(eatenFeeder, false);

			List<Integer> enabled = fu.getEnabledFeeders();
			enabled.remove(new Integer(eatenFeeder));
			// for (Integer f : enabled){
			// u.setActiveFeeder(f, true);
			//
			// }

			// Activate only one
			fu.setActiveFeeder(enabled.get(random.nextInt(enabled.size())), true);
		}
	}

}
