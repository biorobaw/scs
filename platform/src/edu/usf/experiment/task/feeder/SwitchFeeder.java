package edu.usf.experiment.task.feeder;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
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

	public void perform(Universe u, Subject s) {
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");

		FeederUniverse fu = (FeederUniverse) u;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;

		if (fu.hasRobotEaten()) {
			int eatenFeeder = FeederUniverseUtilities.getFeedingFeeder(fu.getFeeders(), gcu.getRobotPosition(),
					fu.getCloseThrs());

			// Deactivate the feeding one
			fu.setActiveFeeder(eatenFeeder, false);

			List<Integer> enabled = FeederUniverseUtilities
					.getFeederNums(FeederUniverseUtilities.getEnabledFeeders(fu.getFeeders()));
			enabled.remove(new Integer(eatenFeeder));

			// Activate only one
			fu.setActiveFeeder(enabled.get(random.nextInt(enabled.size())), true);
		}
	}

}
