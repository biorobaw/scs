package edu.usf.ratsim.nsl.modules;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Float1dPortArray;
import edu.usf.micronsl.Module;

public class DecayingExplorationSchema extends Module {

	public float[] votes;
	private float maxReward;
	private Random r;
	private double alpha;

	private Subject subject;
	private LocalizableRobot robot;
	private int episodeCount;
	private Affordance lastPicked;

	public DecayingExplorationSchema(String name, Subject subject,
			LocalizableRobot robot, float maxReward,
			float explorationHalfLifeVal) {
		super(name);
		this.maxReward = maxReward;
		this.alpha = -Math.log(.5) / explorationHalfLifeVal;

		votes = new float[subject.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		r = RandomSingleton.getInstance();

		episodeCount = 0;

		this.subject = subject;
		this.robot = robot;

		this.lastPicked = null;
	}

	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Flashing feeder prevents exploration
		// if (!robot.seesFlashingFeeder()){
		double explorationValue = maxReward
				* Math.exp(-(episodeCount - 1) * alpha);
		List<Affordance> affs = robot.checkAffordances(subject
				.getPossibleAffordances());

		// Avoid alternating rotations as exploration
		// if (lastPicked != null && lastPicked instanceof TurnAffordance &&
		// turnRealizable(affs, (TurnAffordance)lastPicked))
		// affs = removeOtherTurns(affs, (TurnAffordance) lastPicked);

		Affordance pickedAffordance;
		do {
			if (containForward(affs) && r.nextBoolean())
				pickedAffordance = getForward(affs);
			pickedAffordance = affs.get(r.nextInt(affs.size()));
		} while (!pickedAffordance.isRealizable()
				|| (pickedAffordance instanceof EatAffordance));

		votes[pickedAffordance.getIndex()] = (float) explorationValue;

		lastPicked = pickedAffordance;
		// }
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

	private List<Affordance> removeOtherTurns(List<Affordance> affs,
			TurnAffordance turn) {
		for (Iterator<Affordance> iter = affs.iterator(); iter.hasNext();) {
			Affordance aff = iter.next();
			if (aff instanceof TurnAffordance
					&& ((TurnAffordance) aff).getAngle() != turn.getAngle())
				iter.remove();
		}

		return affs;
	}

	public void newEpisode() {
		episodeCount++;
	}

	public void newTrial() {
		episodeCount = 0;
	}

	public void setExplorationVal(float val) {
		maxReward = val;
	}

	@Override
	public boolean usesRandom() {
		return true;
	}

}
