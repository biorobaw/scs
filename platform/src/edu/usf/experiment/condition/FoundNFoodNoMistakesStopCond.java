package edu.usf.experiment.condition;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.Episode;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.FeederUniverse;
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
		Universe u = episode.getUniverse();
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		Subject sub = episode.getSubject();
		if (sub.hasTriedToEat() && fu.getFoundFeeder() != -1) {
			if (!fu.isFeederEnabled(fu.getFoundFeeder())) {
				// Trying to eat from wrong feeder
				toGo = n;
			} else if (fu.getFoundFeeder() != lastFeeder) {
				// Trying to eat from an enabled feeder
				flashing.add(fu.isFeederFlashing(fu.getFoundFeeder()));
				toGo--;
				lastFeeder = fu.getFoundFeeder();
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
