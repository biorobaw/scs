package edu.usf.ratsim.model.morris.multiscale;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.model.ValueModel;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.concat.Float1dSparseConcatModule;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.module.sum.Float1dSumModule;
import edu.usf.micronsl.plot.Float1dSeriesPlot;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.DecayingExplorationSchema;
import edu.usf.ratsim.nsl.modules.actionselection.GradientValue;
import edu.usf.ratsim.nsl.modules.actionselection.GradientVotes;
import edu.usf.ratsim.nsl.modules.actionselection.NoExploration;
import edu.usf.ratsim.nsl.modules.actionselection.Voter;
import edu.usf.ratsim.nsl.modules.cell.ConjCell;
import edu.usf.ratsim.nsl.modules.celllayer.RndHDPCellLayer;
import edu.usf.ratsim.nsl.modules.input.SubFoundPlatform;
import edu.usf.ratsim.nsl.modules.rl.MultiStateACNoTraces;
import edu.usf.ratsim.nsl.modules.rl.QLAlgorithm;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class ActorCriticModel extends Model implements ValueModel{

	// private ProportionalExplorer actionPerformerVote;
	// private List<WTAVotes> qLActionSel;
	private List<DecayingExplorationSchema> exploration;
	private Float1dSparseConcatModule jointPCHDIntentionState;
	private GradientValue rlValue;
	private List<RndHDPCellLayer> conjCellLayers;
	private float[][] value;
	private int numActions;
	private QLAlgorithm rlAlg;
	private MultiStateACNoTraces msac;
	private List<Float> valueConnProbs;

	public ActorCriticModel() {
	}

	public ActorCriticModel(ElementWrapper params, Robot robot) {
		/**
		 * Place cell radius range
		 */
		float minPCRadius = params.getChildFloat("minPCRadius");
		float maxPCRadius = params.getChildFloat("maxPCRadius");
		/**
		 * Number of cell layers, each layer uses the same parameters for each
		 * cell
		 */
		int numCCLayers = params.getChildInt("numCCLayers");
		/**
		 * Lengths of the layers for bupi modulation purposes
		 */
		List<Integer> layerLengths = params.getChildIntList("layerLengths");
		/**
		 * Numbers of cells per layer
		 */
		List<Integer> numCCCellsPerLayer = params.getChildIntList("numCCCellsPerLayer");
		/**
		 * Radius for head direction modulation
		 */
		float minHDRadius = params.getChildFloat("minHDRadius");
		float maxHDRadius = params.getChildFloat("maxHDRadius");
		/**
		 * Reinforcement learning discount factor
		 */
		float rlDiscountFactor = params.getChildFloat("rlDiscountFactor");
		/**
		 * Learning rate
		 */
		float alpha = params.getChildFloat("alpha");

		/**
		 * Reward given upon eating
		 */
		float foodReward = params.getChildFloat("foodReward");
		/**
		 * Reward given in non-food obtaining steps
		 */
		float nonFoodReward = params.getChildFloat("nonFoodReward");
		/**
		 * The action-value applied to exploration actions (should be called
		 * explorationActionValue)
		 */
		float explorationReward = params.getChildFloat("explorationReward");
		/**
		 * Half life parameter for the decaying exploration
		 */
		float explorationHalfLifeVal = params.getChildFloat("explorationHalfLifeVal");
		/**
		 * Bounding box for place cell locations
		 */
		float xmin = params.getChildFloat("xmin");
		float ymin = params.getChildFloat("ymin");
		float xmax = params.getChildFloat("xmax");
		float ymax = params.getChildFloat("ymax");
		
		AffordanceRobot aRobot = (AffordanceRobot) robot;
		FeederRobot fRobot = (FeederRobot) robot;
		LocalizableRobot lRobot = (LocalizableRobot) robot;

		numActions = aRobot.getPossibleAffordances().size();

		// qLActionSel = new LinkedList<WTAVotes>();
		exploration = new LinkedList<DecayingExplorationSchema>();

		// Create the layers
		float radius = minPCRadius;
		conjCellLayers = new LinkedList<RndHDPCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();
		// For each layer
		for (int i = 0; i < numCCLayers; i++) {
			RndHDPCellLayer ccl = new RndHDPCellLayer("CCL " + i, robot, radius, minHDRadius, maxHDRadius,
					numCCCellsPerLayer.get(i), "ExponentialHDPC", xmin, ymin, xmax, ymax,
					layerLengths.get(i));
			conjCellLayers.add(ccl);
			conjCellLayersPorts.add(ccl.getOutPort("activation"));
			addModule(ccl);
			radius += (maxPCRadius - minPCRadius) / (numCCLayers - 1);
		}

		// Concatenate all layers
		jointPCHDIntentionState = new Float1dSparseConcatModule("Joint PC HD Intention State");
		jointPCHDIntentionState.addInPorts(conjCellLayersPorts);
		addModule(jointPCHDIntentionState);

		// Copy last state and votes before recomputing to use in RL algorithm
		Float1dSparseCopyModule stateCopy = new Float1dSparseCopyModule("States Before");
		stateCopy.addInPort("toCopy", jointPCHDIntentionState.getOutPort("jointState"), true);
		addModule(stateCopy);

		// Create value matrix
		int numStates = ((Float1dPort) jointPCHDIntentionState.getOutPort("jointState")).getSize();
		value = new float[numStates][numActions + 1];
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		// Take the value of each state and vote for an action
		List<Port> votesPorts = new LinkedList<Port>();
		Module rlVotes;
		List<Float> connProbs = params.getChildFloatList("votesConnProbs");
		float votesNormalizer = params.getChildFloat("votesNormalizer");
		rlVotes = new GradientVotes("RL votes", numActions, connProbs, numCCCellsPerLayer, votesNormalizer, foodReward);
		// RL votes are based on previous state
		rlVotes.addInPort("states", stateCopy.getOutPort("copy"));
		rlVotes.addInPort("value", valuePort);
		addModule(rlVotes);
		votesPorts.add((Float1dPort) rlVotes.getOutPort("votes"));

		// Exploration module
		DecayingExplorationSchema decayExpl = new DecayingExplorationSchema("Decay Explorer", robot,
				explorationReward, explorationHalfLifeVal);
		exploration.add(decayExpl);
		addModule(decayExpl);
		votesPorts.add((Float1dPort) decayExpl.getOutPort("votes"));

		// Joint votes
		Float1dSumModule jointVotes = new Float1dSumModule("Votes");
		jointVotes.addInPorts(votesPorts);
		addModule(jointVotes);

		// Get votes from QL and other behaviors and perform an action
		// One vote per layer (one now) + taxic + wf
//		ProportionalExplorer actionPerformer = new ProportionalExplorer("Action Performer", subject);
		NoExploration actionPerformer = new NoExploration("Action Performer", robot);
		actionPerformer.addInPort("votes", jointVotes.getOutPort("jointState"));
		addModule(actionPerformer);
		// State calculation should be done after movement
		for (RndHDPCellLayer ccl : conjCellLayers)
			ccl.addPreReq(actionPerformer);

		Port takenActionPort = actionPerformer.getOutPort("takenAction");

		// RL votes to consolidate cell activity and action-value to votes
		// if (voteType.equals("halfAndHalfConnection"))
		// rlValue = new HalfAndHalfConnectionValue("RL value estimation",
		// numActions, cellContribution);
		// else if (voteType.equals("proportional"))
		// rlValue = new ProportionalValue("RL estimation", numActions);
		// else if (voteType.equals("gradient")) {
		valueConnProbs = params.getChildFloatList("valueConnProbs");
		float valueNormalizer = params.getChildFloat("valueNormalizer");
		rlValue = new GradientValue("RL value estimation", numActions, valueConnProbs, numCCCellsPerLayer,
				valueNormalizer,foodReward);
		// } else
		// throw new RuntimeException("Vote mechanism not implemented");
		rlValue.addInPort("states", jointPCHDIntentionState.getOutPort("jointState"));
		rlValue.addInPort("value", valuePort);
		rlValue.addInPort("takenAction", takenActionPort); // just for
															// dependency
		addModule(rlValue);

		// Copy the previous estimation of rl value
		Float1dCopyModule rlValueCopy = new Float1dCopyModule("RL Value Estimation Before");
		rlValueCopy.addInPort("toCopy", (Float1dPort) rlValue.getOutPort("valueEst"), true);
		addModule(rlValueCopy);

		SubFoundPlatform foundPlat = new SubFoundPlatform("sub found platform", robot);
		foundPlat.addInPort("takenAction", takenActionPort); // dep
		addModule(foundPlat);

		// The obtained reward last cycle
		Reward reward = new Reward("Reward", foodReward, nonFoodReward);
		reward.addInPort("rewardingEvent", foundPlat.getOutPort("foundPlatform"));
		addModule(reward);

		msac = new MultiStateACNoTraces("RL Module", numActions, numStates, rlDiscountFactor, alpha);

		msac.addInPort("reward", reward.getOutPort("reward"));
		msac.addInPort("takenAction", takenActionPort);
		msac.addInPort("statesBefore", getModule("States Before").getOutPort("copy"));
		msac.addInPort("statesAfter", jointPCHDIntentionState.getOutPort("jointState"));
		msac.addInPort("value", valuePort);
		msac.addInPort("rlValueEstimationAfter", rlValue.getOutPort("valueEst"));
		msac.addInPort("rlValueEstimationBefore", getModule("RL Value Estimation Before").getOutPort("copy"));
		addModule(msac);
		rlAlg = msac;
		
		
		// Displays
		DisplaySingleton.getDisplay().addPanel(new Float1dSeriesPlot((Float1dPort)jointVotes.getOutPort("jointState")), 0, 0, 1, 1);
	}

	public List<RndHDPCellLayer> getPCLLayers() {
		return conjCellLayers;
	}

	public void newTrial() {
		// for (DecayingExplorationSchema gs : exploration)
		// gs.newTrial();
	}

	public void deactivatePCLRadial(List<Integer> layersToDeactivate, float constant) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeRadial(constant);
		}
	}

	public void deactivatePCLProportion(List<Integer> layersToDeactivate, float proportion) {
		for (Integer layer : layersToDeactivate) {
			System.out.println("[+] Deactivating layer " + layer);
			conjCellLayers.get(layer).anesthtizeProportion(proportion);
		}
	}

	public void newEpisode() {
		for (DecayingExplorationSchema gs : exploration)
			gs.newEpisode();

		rlAlg.newEpisode();

		for (RndHDPCellLayer layer : conjCellLayers)
			layer.clear();
	}

	public void setExplorationVal(float val) {
		for (DecayingExplorationSchema e : exploration)
			e.setExplorationVal(val);

	}

	public Map<Float, Float> getValue(Point3f point, int inte, float angleInterval, float distToWall) {

		Map<Float, Float> angleValue = new HashMap<Float, Float>();
		for (float angle = 0; angle <= 2 * Math.PI; angle += angleInterval) {
			for (RndHDPCellLayer ccl : conjCellLayers)
				ccl.run(point, angle, distToWall);

			jointPCHDIntentionState.run();

			rlValue.run();

			float[] votes = ((Voter) rlValue).getVotes();
			angleValue.put(angle, votes[0]);
		}

		for (RndHDPCellLayer ccl : conjCellLayers)
			ccl.clear();

		return angleValue;
	}

	public List<ConjCell> getPlaceCells() {
		List<ConjCell> res = new LinkedList<ConjCell>();
		for (RndHDPCellLayer ccl : conjCellLayers) {
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
		for (RndHDPCellLayer layer : conjCellLayers)
			activation.putAll(((Float1dSparsePortMap) layer.getOutPort("activation")).getNonZero());
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

	public void replay() {
	}

	public void clearPath() {
	}

	public Map<Point3f, Float> getValuePoints() {
		Map<Point3f, Float> res = new HashMap<Point3f, Float>();
		boolean connected[] = rlValue.getConnectPattern();
		System.out.println(connected.length + " conected booleans");
		boolean anyTrue = false;
		int i = 0, k = 0;
		for (RndHDPCellLayer l : conjCellLayers) {
			float connProb = valueConnProbs.get(i);
			List<ConjCell> cells = l.getCells();
			System.out.println(cells.size() + " number of cells in layer " + i + " with conn prob " + connProb);
			if (connProb != 0)
				for (ConjCell c : cells) {
					if (connected[k]) {
						res.put(c.getPreferredLocation(), value[k][numActions]);
					}
					k++;
				}
			else
				k += cells.size();
			i++;
		}

		return res;
	}
}
