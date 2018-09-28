package edu.usf.ratsim.model.tsp.knownid;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.concat.Float1dSparseConcatModule;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.GoToFeeder;
import edu.usf.ratsim.nsl.modules.actionselection.GradientValue;
import edu.usf.ratsim.nsl.modules.actionselection.GradientVotes;
import edu.usf.ratsim.nsl.modules.celllayer.RndConjCellLayer;
import edu.usf.ratsim.nsl.modules.goaldecider.LastTriedToEatGoalDecider;
import edu.usf.ratsim.nsl.modules.input.ClosestFeeder;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;
import edu.usf.ratsim.nsl.modules.intention.LastAteIntention;
import edu.usf.ratsim.nsl.modules.intention.NoIntention;
import edu.usf.ratsim.nsl.modules.rl.MultiStateAC;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class TSPModelKnownIDAC extends Model {

	// One action per feeder, 0 is invalid
	private int numActions = 6;
	// One intention per feeder (last eaten) + one initial intention
	private int numIntentions = 6;
	// Value table for actions and state-values (Actor Critic)
	private float[][] value;

	public TSPModelKnownIDAC() {
	}

	public TSPModelKnownIDAC(ElementWrapper params, Robot robot) {
		// Environment bounds
		float xmin = params.getChildFloat("xmin");
		float ymin = params.getChildFloat("ymin");
		float xmax = params.getChildFloat("xmax");
		float ymax = params.getChildFloat("ymax");

		// Place cell parameters
		int numCCLayers = params.getChildInt("numCCLayers");
		List<Integer> numCCCellsPerLayer = params.getChildIntList("numCCCellsPerLayer");
		float minPCRadius = params.getChildFloat("minPCRadius");
		float maxPCRadius = params.getChildFloat("maxPCRadius");
		// Lengths needed for deactivation
		List<Integer> layerLengths = params.getChildIntList("layerLengths");

		// Parameters for action voting
		List<Float> connProbs = params.getChildFloatList("votesConnProbs");
		float votesNormalizer = params.getChildFloat("votesNormalizer");
		float valueNormalizer = params.getChildFloat("valueNormalizer");

		// Rewarding params
		float foodReward = params.getChildFloat("foodReward");
		float nonFoodReward = params.getChildFloat("nonFoodReward");

		// RL params
		float rlDiscountFactor = params.getChildFloat("rlDiscountFactor");
		float alpha = params.getChildFloat("alpha");
		float tracesDecay = params.getChildFloat("tracesDecay");

		AffordanceRobot aRobot = (AffordanceRobot) robot;
		FeederRobot fRobot = (FeederRobot) robot;
		LocalizableRobot lRobot = (LocalizableRobot) robot;

		Random r = RandomSingleton.getInstance();

		LastTriedToEatGoalDecider lastTriedToEatGoalDecider = new LastTriedToEatGoalDecider(
				"Last Tried To Eat Goal Decider");
		addModule(lastTriedToEatGoalDecider);

		Module intention;
		if (numIntentions > 1) {
			intention = new LastAteIntention("Intention", numIntentions);
			intention.addInPort("goalFeeder", lastTriedToEatGoalDecider.getOutPort("goalFeeder"));
		} else {
			intention = new NoIntention("Intention", numIntentions);
		}
		addModule(intention);

		// Create the layers
		float radius = minPCRadius;
		LinkedList<RndConjCellLayer> conjCellLayers = new LinkedList<RndConjCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();
		// For each layer
//		for (int i = 0; i < numCCLayers; i++) {
//			RndConjCellLayer ccl = new RndConjCellLayer("CCL " + i, robot, radius, 0, 0, numIntentions,
//					numCCCellsPerLayer.get(i), "ExponentialPlaceIntentionCell", xmin, ymin, xmax, ymax,
//					layerLengths.get(i), 10, 0);
//			ccl.addInPort("intention", intention.getOutPort("intention"));
//			conjCellLayers.add(ccl);
//			conjCellLayersPorts.add(ccl.getOutPort("activation"));
//			addModule(ccl);
//			radius += (maxPCRadius - minPCRadius) / (numCCLayers - 1);
//		}

		// Concatenate all layers
		Float1dSparseConcatModule jointPCLActivation = new Float1dSparseConcatModule("Joint PC State");
		jointPCLActivation.addInPorts(conjCellLayersPorts);
		addModule(jointPCLActivation);

		// Copy last state and votes before recomputing to use in RL algorithm
		Float1dSparseCopyModule stateCopy = new Float1dSparseCopyModule("States Before");
		stateCopy.addInPort("toCopy", jointPCLActivation.getOutPort("jointState"), true);
		addModule(stateCopy);

		// Create value matrix
		int numStates = ((Float1dPort) jointPCLActivation.getOutPort("jointState")).getSize();
		value = new float[numStates][numActions + 1];
		// Randomize weigths
		for (int s = 0; s < numStates; s++)
			for (int a = 0; a < numActions + 1; a++)
				value[s][a] = (float) r.nextGaussian() * .01f;
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		// Voting mechanism for action selection
		Module rlVotes = new GradientVotes("RL votes", numActions, connProbs, numCCCellsPerLayer, votesNormalizer,
				foodReward);
		rlVotes.addInPort("states", stateCopy.getOutPort("copy"));
		rlVotes.addInPort("value", valuePort);
		addModule(rlVotes);

		// Add in port for dependency
		GoToFeeder gotofeeder = new GoToFeeder("Go To Feeder", robot, RandomSingleton.getInstance());
		// GoToFeedersSequentially gotofeeder = new GoToFeedersSequentially("Go
		// To Feeder", subject,
		// RandomSingleton.getInstance());
		gotofeeder.addInPort("votes", rlVotes.getOutPort("votes"));
		Port takenActionPort = gotofeeder.getOutPort("takenAction");
		addModule(gotofeeder);

		// State calculation should be done after movement
		for (RndConjCellLayer ccl : conjCellLayers)
			ccl.addPreReq(gotofeeder);
		lastTriedToEatGoalDecider.addPreReq(gotofeeder);

		// Information to lastAteGoalDecider about the step
		SubjectTriedToEat subTriedToEat = new SubjectTriedToEat("Subject Tried To Eat", robot);
		subTriedToEat.addInPort("takenAction", takenActionPort);
		addModule(subTriedToEat);
		ClosestFeeder closestFeeder = new ClosestFeeder("Closest Feeder After Move", robot);
		closestFeeder.addInPort("takenAction", takenActionPort);
		addModule(closestFeeder);
		lastTriedToEatGoalDecider.addInPort("subTriedToEat", subTriedToEat.getOutPort("subTriedToEat"));
		lastTriedToEatGoalDecider.addInPort("closestFeeder", closestFeeder.getOutPort("closestFeeder"));

		// Value estimation
		GradientValue rlValue = new GradientValue("RL value estimation", numActions, connProbs, numCCCellsPerLayer,
				valueNormalizer, foodReward);
		rlValue.addInPort("states", jointPCLActivation.getOutPort("jointState"));
		rlValue.addInPort("value", valuePort);
		rlValue.addInPort("takenAction", takenActionPort);
		addModule(rlValue);

		Float1dCopyModule rlValueCopy = new Float1dCopyModule("RL Value Estimation Before");
		rlValueCopy.addInPort("toCopy", (Float1dPort) rlValue.getOutPort("valueEst"), true);
		addModule(rlValueCopy);

		// Rewarding schema
		SubjectAte subAte = new SubjectAte("Subject Ate", robot);
		subAte.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(subAte);
		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("subAte", subAte.getOutPort("subAte"));
		addModule(reward);

		// Actor Critic setup
		MultiStateAC mspac = new MultiStateAC("RL Module", numActions, numStates, rlDiscountFactor, alpha, tracesDecay);
		mspac.addInPort("reward", reward.getOutPort("reward"));
		mspac.addInPort("takenAction", takenActionPort);
		mspac.addInPort("statesBefore", getModule("States Before").getOutPort("copy"));
		mspac.addInPort("statesAfter", jointPCLActivation.getOutPort("jointState"));
		mspac.addInPort("value", valuePort);
		mspac.addInPort("rlValueEstimationAfter", rlValue.getOutPort("valueEst"));
		mspac.addInPort("rlValueEstimationBefore", getModule("RL Value Estimation Before").getOutPort("copy"));
		addModule(mspac);
	}

	public void newTrial() {
	}

	public void newEpisode() {
		// TODO Auto-generated method stub

	}

}
