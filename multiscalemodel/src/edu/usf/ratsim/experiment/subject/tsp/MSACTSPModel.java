package edu.usf.ratsim.experiment.subject.tsp;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.concat.Float1dSparseConcatModule;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.module.sum.Float1dSumModule;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.DecayingExplorationSchema;
import edu.usf.ratsim.nsl.modules.actionselection.GradientValue;
import edu.usf.ratsim.nsl.modules.actionselection.GradientVotes;
import edu.usf.ratsim.nsl.modules.actionselection.NoExploration;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.TaxicFoodManyFeedersManyActionsNotLast;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.TaxicValueSchema;
import edu.usf.ratsim.nsl.modules.celllayer.RndConjCellLayer;
import edu.usf.ratsim.nsl.modules.input.ClosestFeeder;
import edu.usf.ratsim.nsl.modules.input.LastAteFeeder;
import edu.usf.ratsim.nsl.modules.input.LastTriedToEat;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;
import edu.usf.ratsim.nsl.modules.intention.EatCountIntention;
import edu.usf.ratsim.nsl.modules.rl.MultiStateAC;
import edu.usf.ratsim.nsl.modules.rl.MultiStateACNoTraces;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class MSACTSPModel extends Model {

	// Value table for actions and state-values (Actor Critic)
	private float[][] value;
	private LinkedList<DecayingExplorationSchema> exploration;

	public MSACTSPModel() {
	}

	public MSACTSPModel(ElementWrapper params, Subject subject,
			LocalizableRobot lRobot) {
		/*******************************************************************/
		/************************* PARAMS **********************************/
		/*******************************************************************/
		// Environment bounds
		float xmin = params.getChildFloat("xmin");
		float ymin = params.getChildFloat("ymin");
		float xmax = params.getChildFloat("xmax");
		float ymax = params.getChildFloat("ymax");

		// Place cell parameters
		int numCCLayers = params.getChildInt("numCCLayers");
		List<Integer> numCCCellsPerLayer = params
				.getChildIntList("numCCCellsPerLayer");
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
		float feederReward = params.getChildFloat("feederReward");
		
		int numFeeders = params.getChildInt("numFeeders");
		
		// RL params
		float rlDiscountFactor = params.getChildFloat("rlDiscountFactor");
		float taxicDiscountFactor = rlDiscountFactor;
		float alpha = params.getChildFloat("alpha");
		
		/**
		 * The action-value applied to exploration actions (should be called
		 * explorationActionValue)
		 */
		float explorationReward = params.getChildFloat("explorationReward");
		/**
		 * Half life parameter for the decaying exploration
		 */
		float explorationHalfLifeVal = params.getChildFloat("explorationHalfLifeVal");
		
		Random r = RandomSingleton.getInstance();
		
		int numActions = subject.getPossibleAffordances().size();
		
		/*******************************************************************/
		/************************* INPUT ***********************************/
		/*******************************************************************/
		
		// Information to lastAteGoalDecider about the step
		SubjectTriedToEat subTriedToEat = new SubjectTriedToEat(
				"Subject Tried To Eat", subject);
		addModule(subTriedToEat);
		ClosestFeeder closestFeeder = new ClosestFeeder(
				"Closest Feeder After Move", subject);
		addModule(closestFeeder);
		// Rewarding schema
		SubjectAte subAte = new SubjectAte("Subject Ate", subject);
		addModule(subAte);
		LastTriedToEat lastAteFeeder = new LastTriedToEat("Last Tried Ate Feeder", subject);
		addModule(lastAteFeeder);
		
		/*******************************************************************/
		/************************* STATE ***********************************/
		/*******************************************************************/
		
		// One intention per feeder, changes upon eating
		Module intention = new EatCountIntention("Eat count intention", 5, subject);
		addModule(intention);

		// Create the layers
		float radius = minPCRadius;
		LinkedList<RndConjCellLayer> conjCellLayers = new LinkedList<RndConjCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();
		// For each layer
		for (int i = 0; i < numCCLayers; i++) {
			RndConjCellLayer ccl = new RndConjCellLayer("CCL " + i, lRobot,
					radius, 0, 0, numFeeders, numCCCellsPerLayer.get(i),
					"ExponentialPlaceIntentionCell", xmin, ymin, xmax, ymax,
					lRobot.getAllFeeders(), 0, layerLengths.get(i), 0);
			ccl.addInPort("intention", intention.getOutPort("intention"));
			conjCellLayers.add(ccl);
			conjCellLayersPorts.add(ccl.getOutPort("activation"));
			addModule(ccl);
			radius += (maxPCRadius - minPCRadius) / (numCCLayers - 1);
		}

		// Concatenate all layers
		Float1dSparseConcatModule jointPCLActivation = new Float1dSparseConcatModule(
				"Joint PC State");
		jointPCLActivation.addInPorts(conjCellLayersPorts);
		addModule(jointPCLActivation);

		// Copy last state and votes before recomputing to use in RL algorithm
		Float1dSparseCopyModule stateCopy = new Float1dSparseCopyModule(
				"States Before");
		stateCopy.addInPort("toCopy",
				jointPCLActivation.getOutPort("jointState"), true);
		addModule(stateCopy);

		// Create value matrix
		int numStates = ((Float1dPort) jointPCLActivation
				.getOutPort("jointState")).getSize();
		value = new float[numStates][numActions + 1];
		// Randomize weigths
		for (int s = 0; s < numStates; s++)
			for(int a = 0; a < numActions + 1; a++)
				value[s][a] = (float) r.nextGaussian() * .01f;
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		/*******************************************************************/
		/************************* VOTES ***********************************/
		/*******************************************************************/
		// Voting mechanism for action selection
		List<Port> votesPorts = new LinkedList<Port>();
		
		// RL Votes
		Module rlVotes = new GradientVotes("RL votes", numActions, connProbs,
				numCCCellsPerLayer, votesNormalizer, foodReward);
		rlVotes.addInPort("states", stateCopy.getOutPort("copy"));
		rlVotes.addInPort("value", valuePort);
		addModule(rlVotes);
		votesPorts.add(rlVotes.getOutPort("votes"));
		
		// Exploration votes
		DecayingExplorationSchema decayExpl = new DecayingExplorationSchema("Decay Explorer", subject, lRobot,
				explorationReward, explorationHalfLifeVal);
		exploration = new LinkedList<DecayingExplorationSchema>();
		exploration.add(decayExpl);
		addModule(decayExpl);
		votesPorts.add(decayExpl.getOutPort("votes"));
		
//		// Food votes
		TaxicFoodManyFeedersManyActionsNotLast taxicff = new TaxicFoodManyFeedersManyActionsNotLast(
				"Taxic Food Finder", subject, lRobot, feederReward,
				nonFoodReward, taxicDiscountFactor);
		taxicff.addInPort("lastAteFeeder", lastAteFeeder.getOutPort("lastAteFeeder"),true);
		addModule(taxicff);
		votesPorts.add(taxicff.getOutPort("votes"));

		// Joint votes
		Float1dSumModule jointVotes = new Float1dSumModule("Votes");
		jointVotes.addInPorts(votesPorts);
		addModule(jointVotes);
		
		/*******************************************************************/
		/************************* ACTION PERF *****************************/
		/*******************************************************************/

		NoExploration actionPerformer = new NoExploration("Action Performer", subject);
		actionPerformer.addInPort("votes", jointVotes.getOutPort("jointState"));
		addModule(actionPerformer);
		// State calculation should be done after movement
		for (RndConjCellLayer ccl : conjCellLayers)
			ccl.addPreReq(actionPerformer);
		intention.addPreReq(actionPerformer);

		
		// Add dependencies to movement module
		subTriedToEat.addPreReq(actionPerformer);
		closestFeeder.addPreReq(actionPerformer);
		subAte.addPreReq(actionPerformer);
		lastAteFeeder.addPreReq(actionPerformer);
		
		Port takenActionPort = actionPerformer.getOutPort("takenAction");
		
		/*******************************************************************/
		/************************* VALUE ***********************************/
		/*******************************************************************/
		// Ports to make summation
		List<Port> valueBeforePorts = new LinkedList<Port>();
		List<Port> valueAfterPorts = new LinkedList<Port>();
		
		TaxicValueSchema taxVal = new TaxicValueSchema("Taxic Value Estimator",
				subject, lRobot, nonFoodReward, nonFoodReward,
				taxicDiscountFactor, true);
		taxVal.addInPort("lastAteFeeder",
				lastAteFeeder.getOutPort("lastAteFeeder"));
		taxVal.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(taxVal);
		valueAfterPorts.add(taxVal.getOutPort("value"));
		
		Float1dCopyModule taxicValueCopy = new Float1dCopyModule(
				"Taxic Value Estimation Before");
		taxicValueCopy.addInPort("toCopy",
				(Float1dPort) taxVal.getOutPort("value"), true);
		addModule(taxicValueCopy);
		valueBeforePorts.add(taxicValueCopy.getOutPort("copy"));
		
		// Value estimation
		GradientValue rlValue = new GradientValue("RL value estimation", numActions,
				connProbs, numCCCellsPerLayer, valueNormalizer, foodReward);
		rlValue.addInPort("states",
				jointPCLActivation.getOutPort("jointState"));
		rlValue.addInPort("value", valuePort);
		rlValue.addInPort("takenAction", takenActionPort);
		addModule(rlValue);
		valueAfterPorts.add(rlValue.getOutPort("valueEst"));
		
		Float1dCopyModule rlValueCopy = new Float1dCopyModule(
				"RL Value Estimation Before");
		rlValueCopy.addInPort("toCopy",
				(Float1dPort) rlValue.getOutPort("valueEst"), true);
		addModule(rlValueCopy);
		valueBeforePorts.add(rlValueCopy.getOutPort("copy"));
		
		Float1dSumModule sumValueBefore = new Float1dSumModule(
				"Joint value estimation Before");
		sumValueBefore.addInPorts(valueBeforePorts);
		addModule(sumValueBefore);
		
		Float1dSumModule sumValueAfter = new Float1dSumModule(
				"Joint value estimation after");
		sumValueAfter.addInPorts(valueAfterPorts);
		addModule(sumValueAfter);
		
		/*******************************************************************/
		/**************************** RL ***********************************/
		/*******************************************************************/
		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("rewardingEvent", subAte.getOutPort("subAte"));
		addModule(reward);
	
		// Actor Critic setup
		MultiStateACNoTraces mspac = new MultiStateACNoTraces(
				"RL Module",numActions, numStates,
				rlDiscountFactor, alpha);
		mspac.addInPort("reward", reward.getOutPort("reward"));
		mspac.addInPort("takenAction", takenActionPort);
		mspac.addInPort("statesBefore", getModule("States Before")
				.getOutPort("copy"));
		mspac.addInPort("statesAfter",
				jointPCLActivation.getOutPort("jointState"));
		mspac.addInPort("value", valuePort);
		mspac.addInPort("rlValueEstimationAfter",
				sumValueAfter.getOutPort("jointState"));
		mspac.addInPort("rlValueEstimationBefore",
				sumValueBefore.getOutPort("jointState"));
		addModule(mspac);
	}

	public void newTrial() {
	}

	public void newEpisode() {
		// TODO Auto-generated method stub

	}

}
