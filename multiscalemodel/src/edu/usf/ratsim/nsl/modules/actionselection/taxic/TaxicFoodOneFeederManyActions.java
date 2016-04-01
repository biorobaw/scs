package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

public class TaxicFoodOneFeederManyActions extends Module {

	public float[] votes;
	private float reward;

	private Subject subject;
	private LocalizableRobot robot;
	private float negReward;

	public TaxicFoodOneFeederManyActions(String name, Subject subject,
			LocalizableRobot robot, float reward, float negReward,
			float lambda, boolean estimateValue) {
		super(name);
		this.reward = reward;
		this.negReward = negReward;

		// Votes for action and value
		votes = new float[subject.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.subject = subject;
		this.robot = robot;
	}

	/**
	 * Assigns the value of executing each action as the value of the next step.
	 * The value is estimated using an exponential discount of the remaining
	 * steps, emulated an already learned value function with a lambda parameter
	 * < 1. The getFeederValue does this. When feeders are lost they cease to
	 * provide value, thus dopamine (delta in rl) falls. Sames happens when
	 * trying to eat and no food is found (due to goalFeeder turning to that
	 * goal).
	 */
	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Get the votes for each affordable action
		List<Affordance> affs = robot.checkAffordances(subject
				.getPossibleAffordances());
		int voteIndex = 0;
		boolean feederToEat = robot.isFeederClose();

		Feeder f = robot.getFeederInFront();
		
		float maxVal = 0;
		int maxIndex = -1;
		for (Affordance af : affs) {
			float value = Float.NEGATIVE_INFINITY;
			if (af.isRealizable()) {
				if (af instanceof TurnAffordance
						|| af instanceof ForwardAffordance) {
					if (!feederToEat) {
						// for (Feeder f : robot.getVisibleFeeders(goalFeeder
						// .getData())) {
						if (f != null) {
							Point3f newPos = GeomUtils.simulate(
									f.getPosition(), af);
							Quat4f rotToNewPos = GeomUtils.angleToPoint(newPos);

							float angleDiff = Math.abs(GeomUtils
									.rotToAngle(rotToNewPos));
							float feederVal;
							if (angleDiff < robot.getHalfFieldView())
								feederVal = getFeederValue(newPos);
							else
								feederVal = -getFeederValue(f.getPosition());
							if (feederVal > value)
								value = feederVal;
						}
						// }
					}
				} else if (af instanceof EatAffordance) {
					if (feederToEat) {
						float feederValue = getFeederValue(robot
								.getClosestFeeder().getPosition());
						if (feederValue > value)
							value = feederValue;
						// value += reward;
					}
				} else
					throw new RuntimeException("Affordance "
							+ af.getClass().getName()
							+ " not supported by robot");
			}

			if (value != Float.NEGATIVE_INFINITY)
				votes[voteIndex] = value;
			else
				votes[voteIndex] = 0;
			voteIndex++;
		}
		
		if (maxIndex != -1)
			votes[maxIndex] = maxVal;

	}

	private float getFeederValue(Point3f feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, subject);
		return (float) Math.max(0, (reward + negReward * steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
