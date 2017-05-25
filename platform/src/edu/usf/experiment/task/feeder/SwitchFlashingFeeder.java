package edu.usf.experiment.task.feeder;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
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
public class SwitchFlashingFeeder extends Task {

	private int feeder;
	private Random random;

	public SwitchFlashingFeeder(ElementWrapper params) {
		super(params);
		random = RandomSingleton.getInstance();
	}

	public void perform(Universe u, Subject s) {
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");

		FeederUniverse fu = (FeederUniverse) u;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;

		if (fu.hasRobotEaten()) {
			int feeder = FeederUniverseUtilities.getFeedingFeeder(fu.getFeeders(), gcu.getRobotPosition(),
					fu.getCloseThrs());

			// Deactivate the feeding one
			fu.setActiveFeeder(feeder, false);
			fu.setFlashingFeeder(feeder, false);

			List<Integer> enabled = FeederUniverseUtilities
					.getFeederNums(FeederUniverseUtilities.getEnabledFeeders(fu.getFeeders()));
			enabled.remove(new Integer(feeder));
			for (Integer f : enabled) {
				// u.setActiveFeeder(f, true);
				fu.setFlashingFeeder(f, false);
			}

			// Pick an active one and flash
			int toFlash = enabled.get(random.nextInt(enabled.size()));
			fu.setActiveFeeder(toFlash, true);
			fu.setFlashingFeeder(toFlash, true);
		}
	}

}
