package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.array.Int1dPortArray;

public class TaxicFoodOneFeederOneAction extends Module {

	public float[] votes;
	private float reward;

	private Subject subject;
	private LocalActionAffordanceRobot ar;
	private FeederRobot fr;
	private float negReward;

	public TaxicFoodOneFeederOneAction(String name, Subject subject,
			LocalizableRobot robot, float reward, float negReward,
			float lambda, boolean estimateValue) {
		super(name);
		this.reward = reward;
		this.negReward = negReward;
		
		this.subject = subject;
		this.ar = (LocalActionAffordanceRobot) robot;
		this.fr = (FeederRobot) robot;

		// Votes for action and value
		votes = new float[ar.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));
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
		Int1dPortArray goalFeeder = (Int1dPortArray) getInPort("goalFeeder");

		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Get the votes for each affordable action
		List<Affordance> affs = ar.checkAffordances(ar
				.getPossibleAffordances());
		int voteIndex = 0;
		boolean feederToEat = fr.isFeederClose();

		Feeder f = fr.getFeederInFront();
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
							Coordinate newPos = GeomUtils.simulate(
									f.getPosition(), af);
							float rotToNewPos = GeomUtils.angleToPoint(newPos);

							float angleDiff = Math.abs(rotToNewPos);
							float feederVal;
							if (angleDiff < fr.getHalfFieldView())
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
						float feederValue = getFeederValue(FeederUtils.getClosestFeeder(fr.getVisibleFeeders())
								.getPosition());
						if (feederValue > value)
							value = feederValue;
						// value += reward;
					}
				} else
					throw new RuntimeException("Affordance "
							+ af.getClass().getName()
							+ " not supported by robot");
			}

			if (value > maxVal){
				maxVal = value;
				maxIndex = voteIndex;
			}
//			if (value != Float.NEGATIVE_INFINITY)
//				votes[voteIndex] = value;
//			else
//				votes[voteIndex] = 0;
			voteIndex++;
		}
		
		if (maxIndex != -1)
			votes[maxIndex] = maxVal;

	}

	private float getFeederValue(Coordinate feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return (float) Math.max(0, (reward + negReward * steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
