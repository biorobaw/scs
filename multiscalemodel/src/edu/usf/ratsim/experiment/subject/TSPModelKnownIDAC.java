package edu.usf.ratsim.experiment.subject;

import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.GoToFeeder;
import edu.usf.ratsim.nsl.modules.actionselection.SingleStateValue;
import edu.usf.ratsim.nsl.modules.input.ClosestFeeder;
import edu.usf.ratsim.nsl.modules.input.LastTriedToEat;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;
import edu.usf.ratsim.nsl.modules.rl.CumulativeReward;
import edu.usf.ratsim.nsl.modules.rl.MultiStateACNoTracesSS;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class TSPModelKnownIDAC extends Model {

	// One action per feeder
	private int numActions = 5;
	// One intention per feeder (last eaten) + one initial intention
	private int numIntentions = 6;
	// One state per action + initial one
	private int numStates = 6;
	// Value table for actions and state-values (Actor Critic)
	private float[][] value;
	private LastTriedToEat lastTriedToEatFeeder;
	private Int0dCopyModule lastTriedCopy;

	public TSPModelKnownIDAC() {
	}

	public TSPModelKnownIDAC(ElementWrapper params, Subject subject,
			LocalizableRobot lRobot) {
		// Rewarding params
		float foodReward = params.getChildFloat("foodReward");
		float nonFoodReward = params.getChildFloat("nonFoodReward");
		
		// RL params
		float rlDiscountFactor = params.getChildFloat("rlDiscountFactor");
		float alpha = params.getChildFloat("alpha");
		
		Random r = RandomSingleton.getInstance();

		// One state per last feeder + 1 initial, one value for each action + one for state val
		value = new float[numStates][numActions+1];
		// Randomize weigths
		for (int s = 0; s < numStates; s++)
			for(int a = 0; a < numActions; a++)
				value[s][a] = (float) r.nextGaussian() * .01f;
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		// Add in port for dependency
		GoToFeeder gotofeeder = new GoToFeeder("Go To Feeder", subject,
				RandomSingleton.getInstance(), numActions);
		Port takenActionPort = gotofeeder.getOutPort("takenAction");
		gotofeeder.addInPort("value", valuePort);
		addModule(gotofeeder);
		
		// Information to lastAteGoalDecider about the step
		SubjectTriedToEat subTriedToEat = new SubjectTriedToEat(
				"Subject Tried To Eat", subject);
		subTriedToEat.addInPort("takenAction", takenActionPort);
		addModule(subTriedToEat);
		gotofeeder.addInPort("subTriedToEat", subTriedToEat.getOutPort("subTriedToEat"), true);
		
		ClosestFeeder closestFeeder = new ClosestFeeder(
				"Closest Feeder After Move", subject);
		closestFeeder.addInPort("takenAction", takenActionPort);
		addModule(closestFeeder);
		
		lastTriedToEatFeeder = new LastTriedToEat("Last tried to eat", subject);
		lastTriedToEatFeeder.addInPort("takenAction", takenActionPort);
		addModule(lastTriedToEatFeeder);
		gotofeeder.addInPort("lastTriedToEatFeeder", lastTriedToEatFeeder.getOutPort("lastTriedToEatFeeder"), true);
		
		// Copy of the last tried to eat feeder
		lastTriedCopy = new Int0dCopyModule("Last tried copy");
		lastTriedCopy.addInPort("toCopy", lastTriedToEatFeeder.getOutPort("lastTriedToEatFeeder"), true);
		addModule(lastTriedCopy);
		
		// Value
		SingleStateValue valueEst = new SingleStateValue("Value est", numActions);
		valueEst.addPreReq(gotofeeder);
		valueEst.addInPort("state", lastTriedToEatFeeder.getOutPort("lastTriedToEatFeeder"));
		valueEst.addInPort("value", valuePort);
		addModule(valueEst);
		
		Float1dCopyModule valueEstBefore = new Float1dCopyModule("Value est before");
		valueEstBefore.addInPort("toCopy", valueEst.getOutPort("valueEst"), true);
		addModule(valueEstBefore);
		
		// Rewarding schema
		SubjectAte subAte = new SubjectAte("Subject Ate", subject);
		subAte.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(subAte);
		
		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("rewardingEvent", subAte.getOutPort("subAte"));
		addModule(reward);
		
		CumulativeReward cumReward = new CumulativeReward("Cumm reward");
		cumReward.addInPort("instantReward", reward.getOutPort("reward"));
		cumReward.addInPort("rewardingEvent", subAte.getOutPort("subAte"));
		cumReward.addInPort("subTriedToEat", subTriedToEat.getOutPort("subTriedToEat"));
		addModule(cumReward);

		// Actor Critic setup
		MultiStateACNoTracesSS mspac = new MultiStateACNoTracesSS(
				"RL Module",numActions, numStates,
				rlDiscountFactor, alpha);
		mspac.addInPort("subTriedToEat", subTriedToEat.getOutPort("subTriedToEat"));
		mspac.addInPort("reward", cumReward.getOutPort("reward"));
		mspac.addInPort("takenAction", takenActionPort);
		mspac.addInPort("statesBefore", lastTriedCopy.getOutPort("copy"));
		mspac.addInPort("statesAfter",lastTriedToEatFeeder.getOutPort("lastTriedToEatFeeder"));
		mspac.addInPort("value", valuePort);
		mspac.addInPort("rlValueEstimationAfter",
				valueEst.getOutPort("valueEst"));
		mspac.addInPort("rlValueEstimationBefore",
				valueEstBefore.getOutPort("copy"));
		addModule(mspac);
	}

	public void newTrial() {
	}

	public void newEpisode() {
		lastTriedToEatFeeder.reset();
		lastTriedCopy.run();
	}

}
