package edu.usf.ratsim.nsl.modules.goaldecider;

import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;

/**
 * Sets the goal to be the flashing feeder (if any) or any feeder if there is
 * none flashing. Does not use the knowledge of which feeders are active.
 * 
 * @author ludo
 * 
 */
public class FlashingOrAnyGoalDecider extends Module {

	public int[] goalFeeder;
	// Keep the goal as a static variable to be able to pass among iterations
	public static int currentGoal;
	private Random r;
	private Subject subject;
	private int numIntentions;
	private static int lastFeeder;

	public FlashingOrAnyGoalDecider(String name, Subject subject,
			int numIntentions) {
		super(name);
		this.subject = subject;
		this.numIntentions = numIntentions;

		goalFeeder = new int[1];
		addOutPort("goalFeeder", new Int1dPortArray(this, goalFeeder));

		r = RandomSingleton.getInstance();

		// Initialize a goal
		currentGoal = -1;
		// List<Integer> feeders = universe.getFeeders();
		// currentGoal = feeders.get(r.nextInt(feeders.size()));

		lastFeeder = -1;
	}

	public void run() {
		if (currentGoal == -1) {
			// currentGoal = 0;
			currentGoal = r.nextInt(numIntentions);
			// System.out.println("Goal in -1 for " + nslGetName());
		}

		// TODO: why do we need the second term?
		if (subject.hasEaten() || subject.hasTriedToEat()) {
			lastFeeder = currentGoal;
			Feeder newFeeder = subject.getRobot().getClosestFeeder(lastFeeder);
			if (newFeeder == null)
				currentGoal = -1;
			else
				currentGoal = newFeeder.getId();
		}

		if (subject.getRobot().seesFlashingFeeder()) {
			currentGoal = subject.getRobot().getFlashingFeeder().getId();
		}

		goalFeeder[0] = currentGoal;

		if (Debug.printAnyGoal)
			System.out.println("Any GD: " + currentGoal + " " + goalFeeder[0]);
	}

	public void newTrial() {
		currentGoal = -1;
	}

	@Override
	public boolean usesRandom() {
		return true;
	}
}
