package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

public class FlashingTaxicValueSchema extends Module {

	public float[] value;
	private float reward;

	private AffordanceRobot ar;
	private FeederRobot fr;
	private double lambda;
	private boolean estimateValue;
	private float negReward;

	public FlashingTaxicValueSchema(String name,
			Robot robot, float reward, float negReward, float lambda,
			boolean estimateValue) {
		super(name);
		this.reward = reward;
		this.negReward = negReward;

		// Votes for action and value
		value = new float[1];
		addOutPort("value", new Float1dPortArray(this, value));

		this.ar = (AffordanceRobot) robot;
		this.fr = (FeederRobot) robot;
		this.lambda = lambda;
		this.estimateValue = estimateValue;
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
		// Get the value of the current position
		if (estimateValue) {
			float val = 0;
			if (fr.seesFlashingFeeder())
				val += getFeederValue(fr.getFlashingFeeder().getPosition());

			// Last position represents the current value
			value[0] = val;
		}
	}

	private float getFeederValue(Point3f feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return Math.max(0, (reward  + negReward * steps)) ; //* Math.pow(lambda, ));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
