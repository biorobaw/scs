package edu.usf.ratsim.model.pablo.multifeeders_martin;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.DrawPanel;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.model.DeactivableModel;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.UniverseLoader;
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
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.platform.drawers.PCDrawer;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.pablo.morris_replay.drawers.RuntimesDrawer;
import edu.usf.ratsim.model.pablo.multifeeders_martin.drawers.FeedingStepHistoryDrawer;
import edu.usf.ratsim.model.pablo.multifeeders_martin.drawers.TryToEatPositionDrawer;
import edu.usf.ratsim.model.pablo.multifeeders_martin.drawers.VoterDrawer;
import edu.usf.ratsim.model.pablo.multifeeders_martin.modules.DistanceToClosesWallModule;
import edu.usf.ratsim.model.pablo.multiplet.drawers.VDrawer;
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
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.SubjectTriedToEat;
import edu.usf.ratsim.nsl.modules.intention.Intention;
import edu.usf.ratsim.nsl.modules.intention.LastAteIntention;
import edu.usf.ratsim.nsl.modules.intention.NoIntention;
import edu.usf.ratsim.nsl.modules.rl.MultiStateACTaxic;
import edu.usf.ratsim.nsl.modules.rl.MultiStateProportionalQL;
import edu.usf.ratsim.nsl.modules.rl.QLAlgorithm;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.vlwsim.universe.VirtUniverse;

public class MultiScaleArtificialPCModel extends Model implements DeactivableModel {

	// private ProportionalExplorer actionPerformerVote;
	// private List<WTAVotes> qLActionSel;
	private List<DecayingExplorationSchema> exploration;
	private Float1dSparseConcatModule jointPCHDIntentionState;
	private Intention intentionGetter;
	private Module rlValue;
	private List<RndConjCellLayer> conjCellLayers;
	private int numActions;
	private QLAlgorithm rlAlg;
	private Float2dSparsePortMatrix valuePort;
	private SubjectAte subAte;
	private Module rlVotes;
	private FlashingTaxicFoodFinderSchema flashingTaxicFF;
	private TaxicFoodManyFeedersManyActionsNotLast taxicff;
	private ObstacleEndTaxic wallTaxic;
	private DecayingExplorationSchema decayExpl;
	private StillExplorer stillExpl;
	private SubjectTriedToEat subTriedToEat;
	private Position position;

	public MultiScaleArtificialPCModel() {
	}

	public MultiScaleArtificialPCModel(ElementWrapper params, Robot robot) {
		

		//==============  CAST ROBOT TO EACH INTERFACE   ================= 
		
		AffordanceRobot aRobot = (AffordanceRobot) robot;
		LocalizableRobot lRobot = (LocalizableRobot) robot;
		
		//====================  LOAD CONFIG PARAMS   ====================  
		
		// Get some configuration values for place cells + qlearning
		float minPCRadius 					= params.getChildFloat("minPCRadius");
		float maxPCRadius 					= params.getChildFloat("maxPCRadius");
		int numCCLayers 					= params.getChildInt("numCCLayers");
		List<Integer> layerLengths 			= params.getChildIntList("layerLengths");
		List<Integer> numCCCellsPerLayer 	= params.getChildIntList("numCCCellsPerLayer");
		float minHDRadius 					= params.getChildFloat("minHDRadius");
		float maxHDRadius 					= params.getChildFloat("maxHDRadius");
		String placeCellType 				= params.getChildText("placeCells");
		float goalCellProportion 			= params.getChildFloat("goalCellProportion");
		float rlDiscountFactor 				= params.getChildFloat("rlDiscountFactor");
		float taxicDiscountFactor 			= params.getChildFloat("taxicDiscountFactor");
		float alpha 						= params.getChildFloat("alpha");
		float tracesDecay 					= params.getChildFloat("tracesDecay");
		float initialValue 					= params.getChildFloat("initialValue");
		float foodReward 					= params.getChildFloat("foodReward");
		float nonFoodReward 				= params.getChildFloat("nonFoodReward");
		int numIntentions 					= params.getChildInt("numIntentions");
		float flashingReward 				= params.getChildFloat("flashingReward");
		float flashingNegReward 			= params.getChildFloat("flashingNegReward");
		float nonFlashingReward 			= params.getChildFloat("nonFlashingReward");
		float nonFlashingNegReward 			= params.getChildFloat("nonFlashingNegReward");
		boolean estimateValue 				= params.getChildBoolean("estimateValue");
		float cellContribution 				= params.getChildFloat("cellContribution");
		float explorationReward 			= params.getChildFloat("explorationReward");
		// float wallFollowingVal 			= params.getChildFloat("wallFollowingVal");
		float wallTaxicVal 					= params.getChildFloat("wallTaxicVal");
		float wallNegReward 				= params.getChildFloat("wallNegReward");
		float wallTooCloseDist 				= params.getChildFloat("wallTooCloseDist");
		
//		float wallParamA 					= params.getChildFloat("wallParamA");
//		float wallParamB 					= params.getChildFloat("wallParamB");
		
		float explorationHalfLifeVal 		= params.getChildFloat("explorationHalfLifeVal");
		float xmin 							= params.getChildFloat("xmin");
		float ymin 							= params.getChildFloat("ymin");
		float xmax 							= params.getChildFloat("xmax");
		float ymax 							= params.getChildFloat("ymax");
		String rlType 						= params.getChildText("rlType");
		String voteType 					= params.getChildText("voteType");
		int maxActionsSinceForward 			= params.getChildInt("maxActionsSinceForward");
		float stillExplorationVal 			= params.getChildFloat("stillExplorationVal");

		numActions  = aRobot.getPossibleAffordances().size();
		exploration = new LinkedList<DecayingExplorationSchema>();

		
		
		//==================== INPUT MODULES =======================
		
		subAte = new SubjectAte("Subject Ate");
//		subAte.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(subAte);

		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("rewardingEvent", subAte.getOutPort("subAte"));
		addModule(reward);		
		
		subTriedToEat = new SubjectTriedToEat("Subject Tried To Eat", robot);
//		subTriedToEat.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(subTriedToEat);

		ClosestFeeder closestFeeder = new ClosestFeeder("Closest Feeder After Move", robot);
//		closestFeeder.addInPort("takenAction", takenActionPort);
		addModule(closestFeeder);

		
		HeadDirection hdModule = new HeadDirection("HD", lRobot);
		addModule(hdModule);
		
		position = new Position("position", lRobot);
		addModule(position);
		
		DistanceToClosesWallModule distanceToWallModule = new DistanceToClosesWallModule("wallDistance", (WallRobot)robot);
		addModule(distanceToWallModule);
		
		
//				lastAteGoalDecider.addInPort("subAte", subAte.getOutPort("subAte"));
//				lastAteGoalDecider.addInPort("closestFeeder",
//						closestFeeder.getOutPort("closestFeeder"));
		
		
		//====================  CREATE INTENTION MODULES  ====================  
		
		//LAST ATE INTENTION
		//If rat just ate, switch lastAteGoalDecider to closest feeder (at new trial set to -1)
		LastAteGoalDecider lastAteGoalDecider = new LastAteGoalDecider("Last Ate Goal Decider");
		lastAteGoalDecider.addInPort("subAte", subAte.getOutPort("subAte"));
		lastAteGoalDecider.addInPort("closestFeeder",closestFeeder.getOutPort("closestFeeder"));
		lastAteGoalDecider.addInPort("subTriedToEat",subTriedToEat.getOutPort("subTriedToEat"));
		addModule(lastAteGoalDecider);
	
		//The intention module converts the goal id into an array of cells where only one is active
		Module intention = numIntentions > 1 ? 	new LastAteIntention("Intention", numIntentions):
												new NoIntention("Intention", numIntentions);
		intention.addInPort("goalFeeder",lastAteGoalDecider.getOutPort("goalFeeder"));
		addModule(intention);
		intentionGetter = (Intention) intention;
//		intention.addPreReq(actionPerformer);
		
		//LAST TRIED TO EAT INTENTION
		//If rat tried to eat, switch lasttTriedToEatGoalDecider to closest feeder (at new trial set to 0)
		LastTriedToEatGoalDecider lastTriedToEatGoalDecider = new LastTriedToEatGoalDecider("Last Tried To Eat Goal Decider");
		lastTriedToEatGoalDecider.addInPort("closestFeeder",closestFeeder.getOutPort("closestFeeder"));
		lastTriedToEatGoalDecider.addInPort("subTriedToEat",subTriedToEat.getOutPort("subTriedToEat"));
		addModule(lastTriedToEatGoalDecider);
		

		
//		beforeActiveGoalDecider = new ActiveGoalDecider(BEFORE_ACTIVE_GOAL_DECIDER_STR, this);		
//		ActiveFeederGoalDecider activeFeederGoalDecider = new ActiveFeederGoalDecider("Active Feeder Goal Decider");
//		addModule(activeFeederGoalDecider);

		
//		OneThenTheOtherGoalDecider oneThenTheOtherGoalDecider = new OneThenTheOtherGoalDecider("One Then The Other Goal Decider",lRobot);
//		addModule(oneThenTheOtherGoalDecider);

		
		
		
		
		
		//==================== ADD TAXIC AND EXPLORATION MODULES ======================= 

		
		
		// flashing feeder taxic module
		flashingTaxicFF = new FlashingTaxicFoodFinderSchema(
										"Flashing Taxic Food Finder", robot, flashingReward,
										flashingNegReward, taxicDiscountFactor, estimateValue);
		addModule(flashingTaxicFF);
		
		// feeder taxic module that avoids going to previous feeder 
		// new GeneralTaxicFoodFinderSchema(BEFORE_FOOD_FINDER_STR, this, robot, universe, numActions, flashingReward, nonFlashingReward);
		taxicff = new TaxicFoodManyFeedersManyActionsNotLast(
				"Taxic Food Finder", robot, nonFlashingReward,
				nonFlashingNegReward, taxicDiscountFactor, estimateValue);
		//the following line of code doesnt seem to be used, thus I comment it out.
//		taxicff.addInPort("goalFeeder",lastTriedToEatGoalDecider.getOutPort("goalFeeder"), true);
		addModule(taxicff);
		
		 
		// Obstacle end taxic module - feels attraction for wall ends
		wallTaxic = new ObstacleEndTaxic("Wall Taxic",robot, wallTaxicVal, wallNegReward, wallTooCloseDist);
		addModule(wallTaxic);
		
		
		// Decaying exploration module - votes for one action, value decays exponentially with episodes
		decayExpl = new DecayingExplorationSchema("Decay Explorer", robot, explorationReward,explorationHalfLifeVal);
		exploration.add(decayExpl);
		addModule(decayExpl);
		
		
		// Constant exploration module that gets activated when rat is stuck
		stillExpl = new StillExplorer("Still Explorer",maxActionsSinceForward, stillExplorationVal, robot);
		addModule(stillExpl);



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
		
		// AvoidWallTaxic avoidWallTaxic = new
		// AvoidWallTaxic("Avoid Wall Taxic", subject, lRobot,
		// avoidWallTaxicVal, avoidWallTaxicDist);
		// addModule(avoidWallTaxic);
		// votesPorts.add((Float1dPort) avoidWallTaxic.getOutPort("votes"));
		
		
		
		
		
		//==================== TAXIC VALUE ESTIMATION  =======================
		
		//add taxic value estimator for non-flashing feeder
		TaxicValueSchema taxVal = new TaxicValueSchema("Taxic Value Estimator",
										robot, nonFlashingReward, nonFoodReward,
										taxicDiscountFactor, estimateValue);
		taxVal.addInPort("goalFeeder",lastTriedToEatGoalDecider.getOutPort("goalFeeder"));
//		taxVal.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(taxVal);
		
		
		//add taxic value estimator for flashing feeder
		FlashingTaxicValueSchema flashTaxVal = new FlashingTaxicValueSchema(
										"Flashing Taxic Value Estimator", robot,
										flashingReward, nonFoodReward, taxicDiscountFactor,
										estimateValue);
		flashTaxVal.addInPort("goalFeeder",lastAteGoalDecider.getOutPort("goalFeeder"));
//		flashTaxVal.addInPort("takenAction", takenActionPort); // just for dependency
		addModule(flashTaxVal);
		
		// add estimated taxic values
		Float1dSumModule sumTaxicValue = new Float1dSumModule("Taxic joint value estimation");
		sumTaxicValue.addInPort(taxVal.getOutPort("value"));
		sumTaxicValue.addInPort(flashTaxVal.getOutPort("value"));
		addModule(sumTaxicValue);

		 
		// copy old taxic value for error estimation 
		Float1dCopyModule taxicValueCopy = new Float1dCopyModule("Taxic Value Estimation Before");
		taxicValueCopy.addInPort("toCopy",(Float1dPort) sumTaxicValue.getOutPort("jointState"), true);
		addModule(taxicValueCopy);
		
		
		
		
		//===============  CREATE PC LAYERS AND VALUE PORTS ================== 		
		
		//layers that will be concatenated
		conjCellLayers = new LinkedList<RndConjCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();

		// Create the layers with different PC sizes
		float deltaRadius = (maxPCRadius - minPCRadius) / (numCCLayers - 1);
		for (int i = 0; i < numCCLayers; i++) {
			float radius = minPCRadius + i*deltaRadius;
			RndConjCellLayer ccl = new RndConjCellLayer("CCL "+ i, radius, minHDRadius, maxHDRadius,
										numIntentions, numCCCellsPerLayer.get(i), placeCellType,
										xmin, ymin, xmax, ymax, layerLengths.get(i));//, wallParamA, wallParamB);
			ccl.addInPort("intention", intention.getOutPort("intention"));
			ccl.addInPort("position",position.getOutPort("position"));
			ccl.addInPort("hd",hdModule.getOutPort("orientation"));
			ccl.addInPort("distanceToWall",distanceToWallModule.getOutPort("distance"));
			conjCellLayers.add(ccl);
			conjCellLayersPorts.add(ccl.getOutPort("activation"));
			addModule(ccl);
		}

		// Concatenate all layers
		jointPCHDIntentionState = new Float1dSparseConcatModule("Joint PC HD Intention State");
		jointPCHDIntentionState.addInPorts(conjCellLayersPorts);
		addModule(jointPCHDIntentionState);

		// State calculation should be done after movement
//		for (RndConjCellLayer ccl : conjCellLayers) ccl.addPreReq(actionPerformer);
		
		//add a copy for the old state
		Float1dSparseCopyModule stateCopy = new Float1dSparseCopyModule("States Before");
		stateCopy.addInPort("toCopy",jointPCHDIntentionState.getOutPort("output"), true);
		addModule(stateCopy);
		
		
		// Create value matrix
		int numStates = ((Float1dPort) jointPCHDIntentionState.getOutPort("output")).getSize();
		valuePort = new Float2dSparsePortMatrix((Module) null, numStates, numActions+1);
		
		
		
		//============== CALCULATE STATE AND ACTION VALUES ==================== 
		
		
		//calculate action values for current state
		switch(voteType) {
			case "proportional":
				rlVotes = new ProportionalVotes("RL votes", numActions,10000);
				break;
			case "gradient":
				List<Float> connProbs = params.getChildFloatList("votesConnProbs");
				float votesNormalizer = params.getChildFloat("votesNormalizer");
				rlVotes = new GradientVotes("RL votes", numActions, connProbs,numCCCellsPerLayer, votesNormalizer, foodReward);
				break;
			case "halfAndHalfConnection":
				rlVotes = new HalfAndHalfConnectionVotes("RL votes", numActions,cellContribution);
				break;
			default:
				throw new RuntimeException("Vote mechanism not implemented");
		}
		rlVotes.addInPort("states", jointPCHDIntentionState.getOutPort("output"));
		rlVotes.addInPort("value", valuePort);
		addModule(rlVotes);

		
		// calculate RL state value function
		switch(voteType) {
			case "halfAndHalfConnection":
				rlValue = new HalfAndHalfConnectionValue("RL value estimation",numActions, cellContribution);
				break;
			case "proportional":
				rlValue = new ProportionalValue("RL  estimation", numActions);
				break;
			case "gradient":
				List<Float> connProbs = params.getChildFloatList("valueConnProbs");
				float valueNormalizer = params.getChildFloat("valueNormalizer");
				rlValue = new GradientValue("RL value estimation", numActions,
						connProbs, numCCCellsPerLayer, valueNormalizer, foodReward);
				break;
			default:
				throw new RuntimeException("Vote mechanism not implemented");	
		}
		rlValue.addInPort("states",jointPCHDIntentionState.getOutPort("output"));
		rlValue.addInPort("value", valuePort);
//		rlValue.addInPort("takenAction", takenActionPort); // just for												// dependency
		addModule(rlValue);

		
		//copy old state value
		Float1dCopyModule rlValueCopy = new Float1dCopyModule("RL Value Estimation Before");
		rlValueCopy.addInPort("toCopy",(Float1dPort) rlValue.getOutPort("valueEst"), true);
		addModule(rlValueCopy);

		
		
		//==================== UPDATE STATE AND ACTION VALUES =======================
		
		//add module to compute error and update state and action values
		Module qlUpdate = null;
		switch(rlType) {
		case "proportionalQl":
			// MultiStateProportionalQLReplay mspql = new MultiStateProportionalQLReplay(
//															QL_STR, this, subject, bAll.getSize(), numActions,
//															discountFactor, alpha, initialValue);
			
			//create copy of old action values
			Float1dCopyModule actionValueCopy = new Float1dCopyModule("Votes Before");
			rlValueCopy.addInPort("toCopy",(Float1dPort) rlValue.getOutPort("valueEst"), true);
			addModule(rlValueCopy);
			
			
			qlUpdate = new MultiStateProportionalQL(
					"RL Module", numActions, taxicDiscountFactor,
					rlDiscountFactor, alpha, initialValue);
			qlUpdate.addInPort("reward",(Float1dPortArray) reward.getOutPort("reward"));
			
			qlUpdate.addInPort("statesBefore", stateCopy.getOutPort("copy"));
			qlUpdate.addInPort("statesAfter",jointPCHDIntentionState.getOutPort("jointState"));
			
			qlUpdate.addInPort("value", valuePort);
			

			qlUpdate.addInPort("votesBefore", actionValueCopy.getOutPort("copy"));
			qlUpdate.addInPort("votesAfter", rlVotes.getOutPort("votes"));
			
			addModule(qlUpdate);
			rlAlg = (MultiStateProportionalQL)qlUpdate;
			break;
			
		case "actorCritic":
			qlUpdate = new MultiStateACTaxic(
					"RL Module",numActions, numStates,
					taxicDiscountFactor, rlDiscountFactor, alpha, tracesDecay);
			qlUpdate.addInPort("reward", reward.getOutPort("reward"));
			
			qlUpdate.addInPort("statesBefore", getModule("States Before").getOutPort("copy"));
			qlUpdate.addInPort("statesAfter",jointPCHDIntentionState.getOutPort("output"));
			
			qlUpdate.addInPort("value", valuePort);
			
			qlUpdate.addInPort("taxicValueEstimationAfter",sumTaxicValue.getOutPort("jointState"));
			qlUpdate.addInPort("taxicValueEstimationBefore",getModule("Taxic Value Estimation Before").getOutPort("copy"));
			
			qlUpdate.addInPort("rlValueEstimationAfter",rlValue.getOutPort("valueEst"));
			qlUpdate.addInPort("rlValueEstimationBefore",getModule("RL Value Estimation Before").getOutPort("copy"));
			addModule(qlUpdate);
			rlAlg = (MultiStateACTaxic)qlUpdate;
			break;
		default:
			throw new RuntimeException("RL mechanism not implemented");
		}
		
		//==================== ACTION SELECTION MODULES =======================
		
		//Create module to sum all action votes
		Float1dSumModule jointVotes = new Float1dSumModule("Votes");
		jointVotes.addInPort(rlVotes.getOutPort("votes"));
		jointVotes.addInPort(taxicff.getOutPort("votes"));
		jointVotes.addInPort(flashingTaxicFF.getOutPort("votes"));
		jointVotes.addInPort(decayExpl.getOutPort("votes"));
		jointVotes.addInPort(stillExpl.getOutPort("votes"));
		jointVotes.addInPort(wallTaxic.getOutPort("votes"));
		addModule(jointVotes);
		
		//add winner take all action performer
		//this module has to be modified since it should only choose the action,
		//not perform it
		NoExploration actionPerformer = new NoExploration("Action Performer",robot);
		actionPerformer.addInPort("votes", jointVotes.getOutPort("jointState"));
		addModule(actionPerformer);
		
		//==================== ADD REVERSE DEPENDENCIES =======================
		
		//add chosen action to still exploration schema
		Port takenActionPort = actionPerformer.getOutPort("takenAction");
		stillExpl.addInPort("takenAction", takenActionPort, true);
		qlUpdate.addInPort("takenAction", takenActionPort);
		
		//==================== Display Functionality =======================
		setDisplay();
		
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

	
	public Map<Float, Float> getValue(Coordinate point, int inte, float angleInterval, float distToWall) {
		
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
		for (int i = 0; i < valuePort.getNRows(); i++)
			entropy += Math.abs(valuePort.get(i,numActions));
		return entropy;
	}

	public void reactivateHPCLayers(List<Integer> indexList) {
		for (Integer layer : indexList) {
			System.out.println("[+] Reactivating layer " + layer);
			conjCellLayers.get(layer).reactivate();
		}
	}

	public void deactivateHPCLayersRadial(List<Integer> layersToDeactivate,
			float constant) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeRadial(constant);
		}
	}

	public void deactivateHPCLayersProportion(List<Integer> layersToDeactivate,
			float proportion) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeProportion(proportion);
		}
	}
	
	
	void setDisplay() {
		Display d = DisplaySingleton.getDisplay();

		d.addPanel(new DrawPanel(300, 300), "panel1", 0, 0, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel2", 1, 0, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel3", 0, 1, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel4", 1, 1, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel5", 0, 2, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel6", 1, 2, 1, 1);
//		d.addPanel(new DrawPanel(300, 300), "panel7", 0, 3, 1, 1);

		// DrawPanel panel2 = new DrawPanel();
		// panel1.setMinimumSize(new Dimension(300, 300));
		// panel1.setPreferredSize(new Dimension(300, 300));
		// panel1.setBackground(Color.red);
		
		
		d.addDrawer("universe", "cycle info", new CycleDataDrawer());
		
		
		var feedingStepHistoryDrawer = new FeedingStepHistoryDrawer(0, 100, subAte);
		feedingStepHistoryDrawer.doLines = false;
		d.addDrawer("panel3", "runtimes", feedingStepHistoryDrawer);
		
		var voterDrawer = new VoterDrawer(numActions);
		voterDrawer.addVoter("RL(G) ",(Float1dPort)rlVotes.getOutPort("votes"), Color.GREEN); // red
		voterDrawer.addVoter("stuck(Y) ",(Float1dPort)stillExpl.getOutPort("votes"), Color.YELLOW); //cyan
		voterDrawer.addVoter("decay(C) \n",(Float1dPort)decayExpl.getOutPort("votes"), Color.CYAN); //gree
		voterDrawer.addVoter("flash(R) ",(Float1dPort)flashingTaxicFF.getOutPort("votes"), Color.RED); //yellow
		voterDrawer.addVoter("feed(B) ",(Float1dPort)taxicff.getOutPort("votes"), Color.BLUE); //blue
		voterDrawer.addVoter("walls(M) ",(Float1dPort)wallTaxic.getOutPort("votes"), Color.MAGENTA); //magenta
		d.addDrawer("panel1", "votes", voterDrawer);

		
		var triedToEatPositionDrawer = new TryToEatPositionDrawer(subTriedToEat, position,subAte);
		d.addDrawer("universe", "cycle info",triedToEatPositionDrawer);
		
		
		
//		PolarDataDrawer qSoftMax = new PolarDataDrawer("Q softmax", modelAwake.softmax.probabilities);
//		RobotDrawer rDrawer = new RobotDrawer((GlobalCameraUniverse)UniverseLoader.getUniverse());
//		PCDrawer pcDrawer 		= new PCDrawer(modelAwake.placeCells.getCells(), modelAwake.placeCells.getActivationPort());
//		pathDrawer = new PathDrawer((LocalizableRobot) lRobot);
//		pathDrawer.setColor(Color.red);
//		wallDrawer.setColor(GuiUtils.getHSBAColor(0f, 0f, 0f, 1));
//		WallDrawer wallDrawer = new WallDrawer(VirtUniverse.getInstance(), 1);
//		VDrawer vdrawer = new VDrawer(modelAwake.getPlaceCells(), VTable);

		

		
		


		
		
		

	}



}
