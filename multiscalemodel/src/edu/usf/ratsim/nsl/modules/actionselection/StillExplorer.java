package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while.
 * 
 * The module goes forward with 50% chance if forward is available. If going forward is not available, 
 * it blindly turns in one direction until forward is available, to scape local minima 
 * 
 * @author biorob
 * 
 */
public class StillExplorer extends Module {

	private static final int TIME_EXPLORING = 1;
	private int maxActionsSinceForward;
	private Subject sub;
	private int actionsSinceForward;
	private Random r;
	private float[] votes;
	private float stillExploringVal;
	public static int timeToExplore;
	private Robot robot;
	private boolean turning;
	private boolean turningLeft;

	public StillExplorer(String name, int maxActionsSinceForward, Subject sub,
			float stillExploringVal) {
		super(name);

		votes = new float[sub.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.maxActionsSinceForward = maxActionsSinceForward;
		this.sub = sub;
		this.stillExploringVal = stillExploringVal;
		this.robot = sub.getRobot();

		actionsSinceForward = 0;
		turning = false;
		turningLeft = false;
		r = RandomSingleton.getInstance();
		timeToExplore = 0;
	}

	public void run() {
		Int0dPort takenAction = (Int0dPort) getInPort("takenAction");

		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		if (takenAction.get() != -1) {
			Affordance taken = sub.getPossibleAffordances().get(
					takenAction.get());

			if (taken instanceof ForwardAffordance)
				actionsSinceForward = 0;
			else
				actionsSinceForward++;
		} else
			actionsSinceForward++;

		// When the agent hasnt moved for a while, add exploring value to random
		// action
		if (timeToExplore > 0 || actionsSinceForward > maxActionsSinceForward) {
			if (timeToExplore == 0)
				timeToExplore = TIME_EXPLORING;
			else
				timeToExplore--;
			
//			System.out.println("Still explorer executing " + turning);

			List<Affordance> affs = robot.checkAffordances(sub
					.getPossibleAffordances());
			Affordance pickedAffordance;
			
			do {
				if (turning){
					if (containForward(affs) && getForward(affs).isRealizable()) { 
						turning = false;
						pickedAffordance = getForward(affs);
					} else 
						pickedAffordance = getRotation(affs, turningLeft);
				} else {
					if (containForward(affs) && getForward(affs).isRealizable())
						if (r.nextBoolean())
							pickedAffordance = getForward(affs);
						else 
							pickedAffordance = affs.get(r.nextInt(affs.size()));
					else {
						turning = true;
						turningLeft = r.nextBoolean();
						pickedAffordance = getRotation(affs, turningLeft);
					}
						
				}
			// Don't pick eating, or an unrealizable affordance (unless turning blindly)
			} while ((pickedAffordance instanceof EatAffordance)
					|| (!turning && !pickedAffordance.isRealizable()));

			votes[affs.indexOf(pickedAffordance)] = stillExploringVal;

			if (Debug.printExploration)
				System.out.println("Performing still exploration");
		}
	}

	private Affordance getRotation(List<Affordance> affs, boolean left) {
		for (Affordance aff : affs)
			if (aff instanceof TurnAffordance)
				if (left && ((TurnAffordance)aff).getAngle() > 0 )
					return aff;
				else if (!left && ((TurnAffordance)aff).getAngle() < 0)
					return aff;
		return null;
	}

	private Affordance getForward(List<Affordance> affs) {
		for (Affordance aff : affs)
			if (aff instanceof ForwardAffordance)
				return aff;

		return null;
	}

	private boolean containForward(List<Affordance> affs) {
		boolean contain = false;
		for (Affordance aff : affs)
			contain = contain || aff instanceof ForwardAffordance;
		return contain;
	}

	@Override
	public boolean usesRandom() {
		return true;
	}
}
