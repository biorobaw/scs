package edu.usf.experiment.condition;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;

public class FoundNFoodNoMistakesStopCond implements Condition {

	private int n;
	private int toGo;
	private List<Boolean> flashing;
	private int lastFeeder;

	public FoundNFoodNoMistakesStopCond(ElementWrapper condParams) {
		n = condParams.getChildInt("n");
		toGo = n;
		flashing = new LinkedList<Boolean>();
		lastFeeder = -1;
	}

	@Override
	public boolean holds() {
		Universe u = Universe.getUniverse();
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) u;
		
		int foundFeeder = FeederUniverseUtilities.getFoundFeeder(fu.getFeeders(), gcu.getRobotPosition(), fu.getCloseThrs());
		if (fu.hasRobotTriedToEat() &&  foundFeeder != -1) {
			if (!fu.getFeeder(foundFeeder).isEnabled()) {
				// Trying to eat from wrong feeder
				toGo = n;
			} else if (foundFeeder != lastFeeder) {
				// Trying to eat from an enabled feeder
				flashing.add(fu.getFeeder(foundFeeder).isFlashing());
				toGo--;
				lastFeeder = foundFeeder;
			}


		}
		if (Debug.printFoundNNoMistakes)
			System.out.println("Feeders to go " + toGo);

		return toGo <= 0 && countFlashing(flashing, n) <= 2;
	}

	private int countFlashing(List<Boolean> flashing, int n) {
		List<Boolean> lastVisited = flashing.subList(flashing.size() - n,
				flashing.size() - 1);
		int count = 0;
		for (Boolean flash : lastVisited)
			if (flash)
				count++;

		return count;
	}

}
