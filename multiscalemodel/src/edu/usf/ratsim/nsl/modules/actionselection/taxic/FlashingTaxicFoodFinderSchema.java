package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

public class FlashingTaxicFoodFinderSchema extends Module {

	public float[] votes;
	private float reward;

	private LocalActionAffordanceRobot ar;
	private FeederRobot fr;
	private float negReward;

	public FlashingTaxicFoodFinderSchema(String name,
			Robot robot, float reward, float negReward,
			float lambda, boolean estimateValue) {
		
		super(name);
		this.ar 		= (LocalActionAffordanceRobot) robot;
		this.fr 		= (FeederRobot) robot;
		this.negReward 	= negReward;
		this.reward 	= reward;

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
		//init votes to 0
		for (int i = 0; i < votes.length; i++) votes[i] = 0;
		
		//If there I cant see a flashing feeder, cant use flashing taxic strategy
		if(!fr.seesFlashingFeeder()) return;

		
		
		//Check if I can eat from flashing feeder
		boolean feederToEat = fr.isFeederClose()
				&& fr.getFlashingFeeder().getId() == FeederUtils.getClosestFeeder(fr.getVisibleFeeders()).getId();
////		System.out.println("Feeder close: " + fr.isFeederClose());
////		System.out.println("Feeder to eat: " + feederToEat);
//		if (fr.seesFlashingFeeder()){
//			System.out.println("Seeing flashing feeder");
//		}
		
		
		// Check whether affordances are realizable, and for each realizable affordance
		// calculate its taxic value and keep the maximum
		List<Affordance> affs = ar.checkAffordances(ar.getPossibleAffordances());
		int voteIndex = 0;
		float maxValue = 0;
		int index = -1;
		for (Affordance af : affs) {
			if (af.isRealizable()) {
				
				//calculate value of this affordance
				float value = 0;
				
				if (af instanceof EatAffordance) {
					
					//eat affordance can only be non zero if close to flashing feeder
					if(feederToEat) value = reward;
					
				} else if(af instanceof TurnAffordance|| af instanceof ForwardAffordance) {
					
					//other affordances are non zero only if cannot eat from the flashing feeder
					if(!feederToEat) {
						Feeder f = fr.getFlashingFeeder();
						Coordinate newPos = GeomUtils.simulate(f.getPosition(), af);
						float rotToNewPos = GeomUtils.angleToPoint(newPos);
						float angleDiff = Math.abs(rotToNewPos);
						if (angleDiff < fr.getHalfFieldView()) value += getFeederValue(newPos);
						else value += -getFeederValue(f.getPosition());
						
					}
				} else throw new RuntimeException("Affordance "+ af.getClass().getName()+ " not supported by robot");
				
				if (value > maxValue) {
					maxValue = value;
					index = voteIndex;
				}
			}
			voteIndex++;
		}
		
		if (index != -1)
			votes[index] = maxValue;
	}

	private float getFeederValue(Coordinate feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return (float) Math.max(0f, (reward + negReward * steps)); // *
																	// Math.pow(lambda,
																	// ));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
