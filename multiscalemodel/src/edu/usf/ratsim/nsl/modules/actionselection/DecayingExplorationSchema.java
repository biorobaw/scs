package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

public class DecayingExplorationSchema extends Module {

	private static final float FORWARD_BIAS = 0.3f;
	public float[] votes;
	private float maxReward;
	private Random r;
	private double alpha;

	private AffordanceRobot robot;
	private int episodeCount;
	private Affordance lastPicked;
	private Float1dPortArray votesPort;

	public DecayingExplorationSchema(String name, Robot robot, float maxReward,
			float explorationHalfLifeVal) {
		super(name);
		
		this.robot = (AffordanceRobot) robot;
		
		this.maxReward = maxReward;
		if (explorationHalfLifeVal != 0)
			this.alpha = -Math.log(.5) / explorationHalfLifeVal;
		else
			this.alpha = 0;

		votes = new float[this.robot.getPossibleAffordances().size()];
		votesPort = new Float1dPortArray(this, votes);
		addOutPort("votes", votesPort);

		r = RandomSingleton.getInstance();

		episodeCount = 0;

		this.lastPicked = null;
	}

	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		double explorationValue = maxReward * Math.exp(-(episodeCount - 1) * alpha);
//		System.out.println(explorationValue);
		List<Affordance> possAffs = robot.getPossibleAffordances();
		List<Affordance> affs = robot.checkAffordances(possAffs);

		List<Affordance> performableAffs = new LinkedList<Affordance>();
		for (Affordance a : affs)
			if (a.isRealizable())
				performableAffs.add(a);

		if (!performableAffs.isEmpty()) {
			Affordance pickedAffordance = null;
			if (containsEat(performableAffs))
				pickedAffordance = getEat(performableAffs);
			else {
				// If last was forward/eat we can turn or forward
				if (lastPicked == null || lastPicked instanceof EatAffordance || lastPicked instanceof ForwardAffordance)
					if (containsForward(performableAffs) && r.nextFloat() < FORWARD_BIAS)
						pickedAffordance = getForward(performableAffs);
					else
						pickedAffordance = performableAffs.get(r.nextInt(performableAffs.size()));
				// But if last was turn, we have to forward or keep turning that way
				else if (lastPicked instanceof TurnAffordance)
					do {
						if (containsForward(performableAffs) && r.nextFloat() < FORWARD_BIAS)
							pickedAffordance = getForward(performableAffs);
						else
							pickedAffordance = performableAffs.get(r.nextInt(performableAffs.size()));
					} while (performableAffs.size() != 1 && pickedAffordance instanceof TurnAffordance &&
							((TurnAffordance)pickedAffordance).getAngle() != ((TurnAffordance)lastPicked).getAngle() );
			}
			
			votes[possAffs.indexOf(pickedAffordance)] = (float) explorationValue;

			lastPicked = pickedAffordance;
		}
		
	}

	private boolean containsForward(List<Affordance> affs) {
		for (Affordance aff : affs)
			if (aff instanceof ForwardAffordance)
				return true;

		return false;
	}

	private boolean containsEat(List<Affordance> affs) {
		for (Affordance aff : affs)
			if (aff instanceof EatAffordance)
				return true;

		return false;
	}

	private Affordance getEat(List<Affordance> affs) {
		for (Affordance aff : affs)
			if (aff instanceof EatAffordance)
				return aff;

		return null;
	}

	private Affordance getForward(List<Affordance> affs) {
		for (Affordance aff : affs)
			if (aff instanceof ForwardAffordance)
				return aff;

		return null;
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
