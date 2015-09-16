package edu.usf.experiment.condition;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
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
	public boolean holds(Episode episode) {
		Subject sub = episode.getSubject();
		Universe u = episode.getUniverse();
		if (sub.hasTriedToEat() && u.getFoundFeeder() != -1) {
			if (!u.isFeederEnabled(u.getFoundFeeder())) {
				// Trying to eat from wrong feeder
				toGo = n;
			} else if (sub.hasEaten()) {
				// Eating from an enabled feeder
				flashing.add(u.isFeederFlashing(u.getFoundFeeder()));
				toGo--;
				lastFeeder = u.getFoundFeeder();
			}
			// If eating from enabled feeder but not the active one, no
			// penalization

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
