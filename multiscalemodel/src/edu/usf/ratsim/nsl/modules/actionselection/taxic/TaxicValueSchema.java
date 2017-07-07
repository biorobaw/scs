package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class TaxicValueSchema extends Module {

	public float[] value;
	private float reward;

	private LocalActionAffordanceRobot ar;
	private FeederRobot fr;
	private double lambda;
	private boolean estimateValue;
	private float negReward;

	public TaxicValueSchema(String name,
			Robot robot, float reward, float negReward, float lambda,
			boolean estimateValue) {
		super(name);
		this.reward = reward;
		this.negReward = negReward;

		// Value estimation
		value = new float[1];
		addOutPort("value", new Float1dPortArray(this, value));

		this.lambda = lambda;
		this.estimateValue = estimateValue;
		
		this.ar = (LocalActionAffordanceRobot) robot;
		this.fr = (FeederRobot) robot;
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
		Int0dPort goalFeeder = (Int0dPort) getInPort("goalFeeder");
		// TODO: see if this is still needed - not being currently excluded
		int exclude[] = {goalFeeder.get()};

		if (fr.getVisibleFeeders().isEmpty())
			value[0] = 0;
		// Get the value of the current position
		else if (estimateValue) {
			value[0] = Float.NEGATIVE_INFINITY;
			for (Feeder f : fr.getVisibleFeeders()) {
//				if (robot.isFeederClose()
//						&& robot.getClosestFeeder().getId() == f.getId())
				float feederValue = getFeederValue(f.getPosition());
				if (feederValue > value[0])
					value[0] = feederValue;
					
			}
			if (value[0] == Float.NEGATIVE_INFINITY)
				value[0] = 0;
		}
	}

	private float getFeederValue(Coordinate feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return Math.max(0, (reward  + negReward * steps)) ; //* Math.pow(lambda, ));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
