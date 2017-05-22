package edu.usf.ratsim.experiment.subject.threefeeders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.concat.Float1dSparseConcatModule;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.module.sum.Float1dSumModule;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.DecayingExplorationSchema;
import edu.usf.ratsim.nsl.modules.actionselection.GradientValue;
import edu.usf.ratsim.nsl.modules.actionselection.GradientVotes;
import edu.usf.ratsim.nsl.modules.actionselection.HalfAndHalfConnectionValue;
import edu.usf.ratsim.nsl.modules.actionselection.HalfAndHalfConnectionVotes;
import edu.usf.ratsim.nsl.modules.actionselection.NoExploration;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalValue;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.StillExplorer;
import edu.usf.ratsim.nsl.modules.actionselection.Voter;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.FlashingTaxicFoodFinderSchema;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.FlashingTaxicValueSchema;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.ObstacleEndTaxic;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.TaxicFoodManyFeedersManyActionsNotLast;
import edu.usf.ratsim.nsl.modules.actionselection.taxic.TaxicValueSchema;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;
import edu.usf.ratsim.nsl.modules.celllayer.RndConjCellLayer;
import edu.usf.ratsim.nsl.modules.goaldecider.LastAteGoalDecider;
import edu.usf.ratsim.nsl.modules.goaldecider.LastTriedToEatGoalDecider;
import edu.usf.ratsim.nsl.modules.input.ClosestFeeder;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;
import edu.usf.ratsim.nsl.modules.intention.Intention;
import edu.usf.ratsim.nsl.modules.intention.LastAteIntention;
import edu.usf.ratsim.nsl.modules.intention.NoIntention;
import edu.usf.ratsim.nsl.modules.rl.MultiStateACTaxic;
import edu.usf.ratsim.nsl.modules.rl.MultiStateProportionalQL;
import edu.usf.ratsim.nsl.modules.rl.QLAlgorithm;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class ThreeFeedersModel extends Model {

	// private ProportionalExplorer actionPerformerVote;
	// private List<WTAVotes> qLActionSel;
	private List<DecayingExplorationSchema> exploration;
	private Float1dSparseConcatModule jointPCHDIntentionState;
	private Intention intentionGetter;
	private Module rlValue;
	private List<RndConjCellLayer> conjCellLayers;
	private float[][] value;
	private int numActions;
	private QLAlgorithm rlAlg;

	public ThreeFeedersModel() {
	}

	public ThreeFeedersModel(ElementWrapper params,
			Robot robot) {
		// Get some configuration values for place cells + qlearning
		float minPCRadius = params.getChildFloat("minPCRadius");
		float maxPCRadius = params.getChildFloat("maxPCRadius");
		int numCCLayers = params.getChildInt("numCCLayers");
		List<Integer> layerLengths = params.getChildIntList("layerLengths");
		List<Integer> numCCCellsPerLayer = params
				.getChildIntList("numCCCellsPerLayer");
		float minHDRadius = params.getChildFloat("minHDRadius");
		float maxHDRadius = params.getChildFloat("maxHDRadius");
		String placeCellType = params.getChildText("placeCells");
		float goalCellProportion = params.getChildFloat("goalCellProportion");
		float rlDiscountFactor = params.getChildFloat("rlDiscountFactor");
		float taxicDiscountFactor = params.getChildFloat("taxicDiscountFactor");
		float alpha = params.getChildFloat("alpha");
		float tracesDecay = params.getChildFloat("tracesDecay");
		float initialValue = params.getChildFloat("initialValue");
		float foodReward = params.getChildFloat("foodReward");
		float nonFoodReward = params.getChildFloat("nonFoodReward");
		int numIntentions = params.getChildInt("numIntentions");
		float flashingReward = params.getChildFloat("flashingReward");
		float flashingNegReward = params.getChildFloat("flashingNegReward");
		float nonFlashingReward = params.getChildFloat("nonFlashingReward");
		float nonFlashingNegReward = params
				.getChildFloat("nonFlashingNegReward");
		boolean estimateValue = params.getChildBoolean("estimateValue");
		float cellContribution = params.getChildFloat("cellContribution");
		float explorationReward = params.getChildFloat("explorationReward");
		// float wallFollowingVal = params.getChildFloat("wallFollowingVal");
		float wallTaxicVal = params.getChildFloat("wallTaxicVal");
		float wallNegReward = params.getChildFloat("wallNegReward");
		float wallTooCloseDist = params.getChildFloat("wallTooCloseDist");
		float wallParamB = params.getChildFloat("wallInhibition");
		float explorationHalfLifeVal = params
				.getChildFloat("explorationHalfLifeVal");
		float xmin = params.getChildFloat("xmin");
		float ymin = params.getChildFloat("ymin");
		float xmax = params.getChildFloat("xmax");
		float ymax = params.getChildFloat("ymax");
		String rlType = params.getChildText("rlType");
		String voteType = params.getChildText("voteType");
		int maxActionsSinceForward = params
				.getChildInt("maxActionsSinceForward");
		float stillExplorationVal = params.getChildFloat("stillExplorationVal");

		AffordanceRobot aRobot = (AffordanceRobot) robot;
		FeederRobot fRobot = (FeederRobot) robot;
		LocalizableRobot lRobot = (LocalizableRobot) robot;
		
		numActions = aRobot.getPossibleAffordances().size();

		// qLActionSel = new LinkedList<WTAVotes>();
		exploration = new LinkedList<DecayingExplorationSchema>();

		// beforeActiveGoalDecider = new ActiveGoalDecider(
		// BEFORE_ACTIVE_GOAL_DECIDER_STR, this);
		LastAteGoalDecider lastAteGoalDecider = new LastAteGoalDecider(
				"Last Ate Goal Decider");
		addModule(lastAteGoalDecider);

		LastTriedToEatGoalDecider lastTriedToEatGoalDecider = new LastTriedToEatGoalDecider(
				"Last Tried To Eat Goal Decider");
		addModule(lastTriedToEatGoalDecider);
//
//		ActiveFeederGoalDecider activeFeederGoalDecider = new ActiveFeederGoalDecider(
//				"Active Feeder Goal Decider");
//		addModule(activeFeederGoalDecider);

		Module intention;
		if (numIntentions > 1) {
			// TODO: fix the issue of goal decider in this model
			intention = new LastAteIntention("Intention", numIntentions);
			intention.addInPort("goalFeeder",
					lastAteGoalDecider.getOutPort("goalFeeder"));
		} else {
			intention = new NoIntention("Intention", numIntentions);
		}
		addModule(intention);
		intentionGetter = (Intention) intention;

		// Create the layers
		float radius = minPCRadius;
		conjCellLayers = new LinkedList<RndConjCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();
		// For each layer
		for (int i = 0; i < numCCLayers; i++) {
			RndConjCellLayer ccl = new RndConjCellLayer("CCL "
					+ i, lRobot, radius, minHDRadius, maxHDRadius,
					numIntentions, numCCCellsPerLayer.get(i), placeCellType,
					xmin, ymin, xmax, ymax, fRobot.getAllFeeders(),
					goalCellProportion, layerLengths.get(i), 10, wallParamB);
			ccl.addInPort("intention", intention.getOutPort("intention"));
			conjCellLayers.add(ccl);
			conjCellLayersPorts.add(ccl.getOutPort("activation"));
			addModule(ccl);
			radius += (maxPCRadius - minPCRadius) / (numCCLayers - 1);
		}

		// Concatenate all layers
		jointPCHDIntentionState = new Float1dSparseConcatModule(
				"Joint PC HD Intention State");
		jointPCHDIntentionState.addInPorts(conjCellLayersPorts);
		addModule(jointPCHDIntentionState);

		// Copy last state and votes before recomputing to use in RL algorithm
		Float1dSparseCopyModule stateCopy = new Float1dSparseCopyModule(
				"States Before");
		stateCopy.addInPort("toCopy",
				jointPCHDIntentionState.getOutPort("jointState"), true);
		addModule(stateCopy);

		// Create value matrix
		int numStates = ((Float1dPort) jointPCHDIntentionState
				.getOutPort("jointState")).getSize();
		value = new float[numStates][numActions + 1];
		// for (int i = 0; i < numStates; i++)
		// value[i][numActions] = .5f;
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		List<Port> votesPorts = new LinkedList<Port>();
		Module rlVotes;
		// Take the value of each state and vote for an action
		if (voteType.equals("proportional"))
			rlVotes = new ProportionalVotes("RL votes", numActions);
		else if (voteType.equals("gradient")) {
			List<Float> connProbs = params.getChildFloatList("votesConnProbs");
			float votesNormalizer = params.getChildFloat("votesNormalizer");
			rlVotes = new GradientVotes("RL votes", numActions, connProbs,
					numCCCellsPerLayer, votesNormalizer, foodReward);
		} else if (voteType.equals("halfAndHalfConnection"))
			rlVotes = new HalfAndHalfConnectionVotes("RL votes", numActions,
					cellContribution);
		else
			throw new RuntimeException("Vote mechanism not implemented");
		// RL votes are based on previous state
		rlVotes.addInPort("states", stateCopy.getOutPort("copy"));
		rlVotes.addInPort("value", valuePort);

		addModule(rlVotes);
		votesPorts.add((Float1dPort) rlVotes.getOutPort("votes"));

		// Create taxic driver
		// new GeneralTaxicFoodFinderSchema(BEFORE_FOOD_FINDER_STR, this, robot,
		// universe, numActions, flashingReward, nonFlashingReward);
		TaxicFoodManyFeedersManyActionsNotLast taxicff = new TaxicFoodManyFeedersManyActionsNotLast(
				"Taxic Food Finder", robot, nonFlashingReward,
				nonFlashingNegReward, taxicDiscountFactor, estimateValue);
		taxicff.addInPort("goalFeeder",
				lastTriedToEatGoalDecider.getOutPort("goalFeeder"), true);
		addModule(taxicff);
		votesPorts.add((Float1dPort) taxicff.getOutPort("votes"));

		FlashingTaxicFoodFinderSchema flashingTaxicFF = new FlashingTaxicFoodFinderSchema(
				"Flashing Taxic Food Finder", robot, flashingReward,
				flashingNegReward, taxicDiscountFactor, estimateValue);
		addModule(flashingTaxicFF);
		votesPorts.add((Float1dPort) flashingTaxicFF.getOutPort("votes"));

		DecayingExplorationSchema decayExpl = new DecayingExplorationSchema(
				"Decay Explorer", robot, explorationReward,
				explorationHalfLifeVal);
		exploration.add(decayExpl);
		addModule(decayExpl);
		votesPorts.add((Float1dPort) decayExpl.getOutPort("votes"));

		StillExplorer stillExpl = new StillExplorer("Still Explorer",
				maxActionsSinceForward, stillExplorationVal, robot);
		addModule(stillExpl);
		votesPorts.add((Float1dPort) stillExpl.getOutPort("votes"));
		// Wall following for obst. avoidance
		// new WallAvoider(BEFORE_WALLAVOID_STR, this, subject,
		// wallFollowingVal,
		// numActions);
		// new TaxicWallOpeningsSchema(BEFORE_WALLFOLLOW_STR, this, subject,
		// lRobot, wallFollowingVal);

		// AttentionalExplorer attExpl = new AttentionalExplorer(
		// "Attentional Explorer", subject, attentionExploringVal,
		// maxAttentionSpan);
		// addModule(attExpl);
		// votesPorts.add((Float1dPort) attExpl.getOutPort("votes"));
		ObstacleEndTaxic wallTaxic = new ObstacleEndTaxic("Wall Taxic",
				robot, wallTaxicVal, wallNegReward, wallTooCloseDist);
		addModule(wallTaxic);
		votesPorts.add((Float1dPort) wallTaxic.getOutPort("votes"));

		// AvoidWallTaxic avoidWallTaxic = new
		// AvoidWallTaxic("Avoid Wall Taxic", subject, lRobot,
		// avoidWallTaxicVal, avoidWallTaxicDist);
		// addModule(avoidWallTaxic);
		// votesPorts.add((Float1dPort) avoidWallTaxic.getOutPort("votes"));

		// Joint votes
		Float1dSumModule jointVotes = new Float1dSumModule("Votes");
		jointVotes.addInPorts(votesPorts);
		addModule(jointVotes);

		// Get votes from QL and other behaviors and perform an action
		// One vote per layer (one now) + taxic + wf
		NoExploration actionPerformer = new NoExploration("Action Performer",
				robot);
		actionPerformer.addInPort("votes", jointVotes.getOutPort("jointState"));
		addModule(actionPerformer);
		// State calculation should be done after movement
		for (RndConjCellLayer ccl : conjCellLayers)
			ccl.addPreReq(actionPerformer);
		intention.addPreReq(actionPerformer);

		Port takenActionPort = actionPerformer.getOutPort("takenAction");
		// Add the taken action ports to some previous exploration modules
		// attExpl.addInPort("takenAction", takenActionPort, true);
		stillExpl.addInPort("takenAction", takenActionPort, true);

		List<Port> taxicValueEstimationPorts = new LinkedList<Port>();
		TaxicValueSchema taxVal = new TaxicValueSchema("Taxic Value Estimator",
				robot, nonFlashingReward, nonFoodReward,
				taxicDiscountFactor, estimateValue);
		taxVal.addInPort("goalFeeder",
				lastTriedToEatGoalDecider.getOutPort("goalFeeder"));
		taxVal.addInPort("takenAction", takenActionPort); // just for dependency
		taxicValueEstimationPorts.add(taxVal.getOutPort("value"));
		addModule(taxVal);

		FlashingTaxicValueSchema flashTaxVal = new FlashingTaxicValueSchema(
				"Flashing Taxic Value Estimator", robot,
				flashingReward, nonFoodReward, taxicDiscountFactor,
				estimateValue);
		flashTaxVal.addInPort("goalFeeder",
				lastAteGoalDecider.getOutPort("goalFeeder"));
		flashTaxVal.addInPort("takenAction", takenActionPort); // just for
																// dependency
		taxicValueEstimationPorts.add(flashTaxVal.getOutPort("value"));
		addModule(flashTaxVal);

		Float1dSumModule sumTaxicValue = new Float1dSumModule(
				"Taxic joint value estimation");
		sumTaxicValue.addInPorts(taxicValueEstimationPorts);
		addModule(sumTaxicValue);

		Float1dCopyModule taxicValueCopy = new Float1dCopyModule(
				"Taxic Value Estimation Before");
		taxicValueCopy.addInPort("toCopy",
				(Float1dPort) sumTaxicValue.getOutPort("jointState"), true);
		addModule(taxicValueCopy);

		if (voteType.equals("halfAndHalfConnection"))
			rlValue = new HalfAndHalfConnectionValue("RL value estimation",
					numActions, cellContribution);
		else if (voteType.equals("proportional"))
			rlValue = new ProportionalValue("RL  estimation", numActions);
		else if (voteType.equals("gradient")) {
			List<Float> connProbs = params.getChildFloatList("valueConnProbs");
			float valueNormalizer = params.getChildFloat("valueNormalizer");
			rlValue = new GradientValue("RL value estimation", numActions,
					connProbs, numCCCellsPerLayer, valueNormalizer, foodReward);
		} else
			throw new RuntimeException("Vote mechanism not implemented");
		rlValue.addInPort("states",
				jointPCHDIntentionState.getOutPort("jointState"));
		rlValue.addInPort("value", valuePort);
		rlValue.addInPort("takenAction", takenActionPort); // just for
															// dependency
		addModule(rlValue);

		Float1dCopyModule rlValueCopy = new Float1dCopyModule(
				"RL Value Estimation Before");
		rlValueCopy.addInPort("toCopy",
				(Float1dPort) rlValue.getOutPort("valueEst"), true);
		addModule(rlValueCopy);

		SubjectAte subAte = new SubjectAte("Subject Ate", robot);
		subAte.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(subAte);

		SubjectTriedToEat subTriedToEat = new SubjectTriedToEat(
				"Subject Tried To Eat", robot);
		subTriedToEat.addInPort("takenAction", takenActionPort); // just for
																	// dependency
		addModule(subTriedToEat);

		ClosestFeeder closestFeeder = new ClosestFeeder(
				"Closest Feeder After Move", robot);
		closestFeeder.addInPort("takenAction", takenActionPort);
		addModule(closestFeeder);

//		lastAteGoalDecider.addInPort("subAte", subAte.getOutPort("subAte"));
//		lastAteGoalDecider.addInPort("closestFeeder",
//				closestFeeder.getOutPort("closestFeeder"));
		lastTriedToEatGoalDecider.addInPort("subTriedToEat",
				subTriedToEat.getOutPort("subTriedToEat"));
		lastTriedToEatGoalDecider.addInPort("closestFeeder",
				closestFeeder.getOutPort("closestFeeder"));
		lastAteGoalDecider.addInPort("subAte", subAte.getOutPort("subAte"));
		lastAteGoalDecider.addInPort("closestFeeder",
				closestFeeder.getOutPort("closestFeeder"));
		lastAteGoalDecider.addInPort("subTriedToEat",
				subTriedToEat.getOutPort("subTriedToEat"));
		
		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("subAte", subAte.getOutPort("subAte"));
		addModule(reward);

		if (rlType.equals("proportionalQl")) {
			// MultiStateProportionalQLReplay mspql = new
			// MultiStateProportionalQLReplay(
			// QL_STR, this, subject, bAll.getSize(), numActions,
			// discountFactor, alpha, initialValue);
			MultiStateProportionalQL mspql = new MultiStateProportionalQL(
					"RL Module", numActions, taxicDiscountFactor,
					rlDiscountFactor, alpha, initialValue);

			mspql.addInPort("reward",
					(Float1dPortArray) reward.getOutPort("reward"));
			mspql.addInPort("takenAction", takenActionPort);
			mspql.addInPort("statesBefore", getModule("States Before")
					.getOutPort("copy"));
			mspql.addInPort("statesAfter",
					jointPCHDIntentionState.getOutPort("jointState"));
			mspql.addInPort("value", valuePort);
			mspql.addInPort("votesBefore", getModule("Votes Before")
					.getOutPort("copy"));
			mspql.addInPort("votesAfter", jointVotes.getOutPort("jointState"));
			addModule(mspql);
			rlAlg = mspql;
		} else if (rlType.equals("actorCritic")) {
			MultiStateACTaxic mspac = new MultiStateACTaxic(
					"RL Module",numActions, numStates,
					taxicDiscountFactor, rlDiscountFactor, alpha, tracesDecay);
			mspac.addInPort("reward", reward.getOutPort("reward"));
			mspac.addInPort("takenAction", takenActionPort);
			mspac.addInPort("statesBefore", getModule("States Before")
					.getOutPort("copy"));
			mspac.addInPort("statesAfter",
					jointPCHDIntentionState.getOutPort("jointState"));
			mspac.addInPort("value", valuePort);
			mspac.addInPort("taxicValueEstimationAfter",
					sumTaxicValue.getOutPort("jointState"));
			mspac.addInPort(
					"taxicValueEstimationBefore",
					getModule("Taxic Value Estimation Before").getOutPort(
							"copy"));
			mspac.addInPort("rlValueEstimationAfter",
					rlValue.getOutPort("valueEst"));
			mspac.addInPort("rlValueEstimationBefore",
					getModule("RL Value Estimation Before").getOutPort("copy"));
			addModule(mspac);
			rlAlg = mspac;
		} else
			throw new RuntimeException("RL mechanism not implemented");
	}

	public List<RndConjCellLayer> getPCLLayers() {
		return conjCellLayers;
	}

	public void newTrial() {
		// anyGoalDecider.newTrial();
		// for(GoalTaxicFoodFinderSchema gs : taxic)
		// gs.newTrial();

		for (DecayingExplorationSchema gs : exploration)
			gs.newTrial();
	}

	public void deactivatePCLRadial(List<Integer> layersToDeactivate,
			float constant) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeRadial(constant);
		}
	}

	public void deactivatePCLProportion(List<Integer> layersToDeactivate,
			float proportion) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeProportion(proportion);
		}
	}

	// protected void finalize() {
	// super.finalize();
	//
	// // System.out.println("NsL model being finalized");
	// }

	// public Voter getQLVotes() {
	// return rlVotes;
	// }

	public void newEpisode() {
		for (DecayingExplorationSchema gs : exploration)
			gs.newEpisode();
		
		rlAlg.newEpisode();
	}

	public void setExplorationVal(float val) {
		for (DecayingExplorationSchema e : exploration)
			e.setExplorationVal(val);

	}

	public Map<Float, Float> getValue(Point3f point, int inte,
			float angleInterval, float distToWall) {
		intentionGetter.run(inte);

		Map<Float, Float> angleValue = new HashMap<Float, Float>();
		for (float angle = 0; angle <= 2 * Math.PI; angle += angleInterval) {
			for (RndConjCellLayer ccl : conjCellLayers)
				ccl.run(point, angle, inte, distToWall);

			jointPCHDIntentionState.run();

			rlValue.run();

			float[] votes = ((Voter) rlValue).getVotes();
			angleValue.put(angle, votes[0]);
		}

		for (RndConjCellLayer ccl : conjCellLayers)
			ccl.clear();

		return angleValue;
	}

	public List<ConjCell> getPlaceCells() {
		List<ConjCell> res = new LinkedList<ConjCell>();
		for (RndConjCellLayer ccl : conjCellLayers) {
			res.addAll(ccl.getCells());
		}

		return res;
	}

	public void remapLayers(LinkedList<Integer> indexList) {
		for (Integer layer : indexList) {
			System.out.println("[+] Remapping layer " + layer);
			conjCellLayers.get(layer).remap();
		}
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		for (RndConjCellLayer layer : conjCellLayers)
			activation.putAll(((Float1dSparsePortMap) layer
					.getOutPort("activation")).getNonZero());
		return activation;
	}

	public float getValueEntropy() {
		float entropy = 0;
		for (int i = 0; i < value.length; i++)
			entropy += Math.abs(value[i][numActions]);
		return entropy;
	}

	public void reactivatePCL(LinkedList<Integer> indexList) {
		for (Integer layer : indexList) {
			System.out.println("[+] Reactivating layer " + layer);
			conjCellLayers.get(layer).reactivate();
		}
	}
}
