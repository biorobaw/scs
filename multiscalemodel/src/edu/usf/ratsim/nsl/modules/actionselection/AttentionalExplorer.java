package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class AttentionalExplorer extends Module {

	private static final float TRACKIN_THRS = .4f;
	private static final float CLOSE_THRS = 0.1f;
	private float[] votes;
	private Subject sub;
	private float exploringVal;
	private LocalActionAffordanceRobot ar;
	private WallRobot wr;
	private Random r;
	private Point3f currentInterest;
	private int attentionRemaining;
	private int maxAttentionSpan;

	public AttentionalExplorer(String name, float exploringVal,
			int maxAttentionSpan) {
		super(name);

		this.ar = (LocalActionAffordanceRobot) sub.getRobot();
		this.wr = (WallRobot) sub.getRobot();

		votes = new float[ar.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.maxAttentionSpan = maxAttentionSpan;
		this.attentionRemaining = 0;
		this.sub = sub;
		this.exploringVal = exploringVal;

		r = RandomSingleton.getInstance();
		currentInterest = null;
	}

	public void run() {
		Int0dPort takenAction = (Int0dPort) getInPort("takenAction");

		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Apply last movement to track interest point
		if (currentInterest != null && takenAction.get() != -1) {
			if (Debug.printAttentional)
				System.out.println("Applying move to track");
			currentInterest = applyLastMove(currentInterest, takenAction.get());
		}

		// Find all visible interest points
		// List<Point3f> interestingPoints = robot.getInterestingPoints();
		List<Point3f> interestingPoints = wr.getVisibleWallEnds();
		// If no current interest or not found, create new interest
		if (attentionRemaining <= 0 // Not interesting any more
				|| currentInterest == null // no current interest
				|| currentInterest.distance(new Point3f()) < CLOSE_THRS // arrived
				|| findClosestPoint(currentInterest, interestingPoints,
						TRACKIN_THRS) == null) { // Lost it
			if (Debug.printAttentional)
				if (currentInterest != null
						&& findClosestPoint(currentInterest, interestingPoints,
								TRACKIN_THRS) == null)
					System.out.println("Lost interest point");
			if (!interestingPoints.isEmpty()) {
				currentInterest = interestingPoints.get(r
						.nextInt(interestingPoints.size()));
				attentionRemaining = maxAttentionSpan;
			} else {
				currentInterest = null;
			}

		} else {
			attentionRemaining--;
		}

		if (currentInterest != null) {
			// Get the current position of the tracking
			currentInterest = findClosestPoint(currentInterest,
					interestingPoints, TRACKIN_THRS);

			// For each affordance set a value based on current interest point
			List<Affordance> affs = ar.checkAffordances(ar
					.getPossibleAffordances());
			int voteIndex = 0;
			for (Affordance af : affs) {
				float value = 0;
				if (af.isRealizable()) {
					if (af instanceof TurnAffordance) {
						value += getFeederValue(
								GeomUtils.simulate(currentInterest, af),
								exploringVal);
					} else if (af instanceof ForwardAffordance) {
						value += getFeederValue(
								GeomUtils.simulate(currentInterest, af),
								exploringVal);
					} else if (af instanceof EatAffordance) {
//						if (robot.isFeederClose()
//								&& robot.getClosestFeeder().getPosition()
//										.distance(currentInterest) < .2f)
//							value += exploringVal;
					} else
						throw new RuntimeException("Affordance "
								+ af.getClass().getName()
								+ " not supported by robot");
				}

				votes[voteIndex] = value;
				voteIndex++;
			}
		}
	}

	private Point3f findClosestPoint(Point3f p, List<Point3f> points,
			float trackinThrs) {
		float minDist = trackinThrs;
		Point3f closest = null;
		for (Point3f p2 : points) {
			// if (Debug.printAttentional)
			// System.out.println("Distance " + p2.distance(p));
			if (p2.distance(p) < minDist) {
				minDist = p2.distance(p);
				closest = p2;
			}
		}

		return closest;
	}

	private Point3f applyLastMove(Point3f p, int i) {
		return GeomUtils.simulate(p, ar.getPossibleAffordances().get(i));
	}

	private float getFeederValue(Point3f feederPos, float reward) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return (float) (reward * Math.pow(.9, steps));
	}

	@Override
	public boolean usesRandom() {
		return true;
	}

}
