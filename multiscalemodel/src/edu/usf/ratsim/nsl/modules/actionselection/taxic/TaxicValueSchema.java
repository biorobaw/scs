package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class TaxicValueSchema extends Module {

	public float[] value;
	private float reward;

	private Subject subject;
	private LocalizableRobot robot;
	private double lambda;
	private boolean estimateValue;
	private float negReward;

	public TaxicValueSchema(String name, Subject subject,
			LocalizableRobot robot, float reward, float negReward, float lambda,
			boolean estimateValue) {
		super(name);
		this.reward = reward;
		this.negReward = negReward;

		// Value estimation
		value = new float[1];
		addOutPort("value", new Float1dPortArray(this, value));

		this.subject = subject;
		this.robot = robot;
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
		Int0dPort lastAte = (Int0dPort) getInPort("lastAteFeeder");
		int except[] = {lastAte.get()};


		if (robot.getVisibleFeeders(except).isEmpty())
			value[0] = 0;
		// Get the value of the current position
		else if (estimateValue) {
			value[0] = Float.NEGATIVE_INFINITY;
			float feederValue;
			for (Feeder f : robot.getVisibleFeeders(except)) {
				if (robot.isFeederClose())
					if (robot.getClosestFeeder().hasFood()){
						feederValue = reward;
					} else {
						feederValue = 0;
					}
				else {
					feederValue = getFeederValue(f.getPosition());
				}
				if (feederValue > value[0])
					value[0] = feederValue;
					
			}
			if (value[0] == Float.NEGATIVE_INFINITY)
				value[0] = 0;
		}
	}

	private float getFeederValue(Point3f feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, subject);
		return Math.max(0, (reward  + negReward * steps)) ; //* Math.pow(lambda, ));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
