package edu.usf.ratsim.experiment.subject;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.concat.Float1dSparseConcatModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.FeederTraveler;
import edu.usf.ratsim.nsl.modules.actionselection.GradientVotes;
import edu.usf.ratsim.nsl.modules.actionselection.HalfAndHalfConnectionVotes;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.RndConjCellLayer;
import edu.usf.ratsim.nsl.modules.celllayer.RndPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.goaldecider.LastTriedToEatGoalDecider;
import edu.usf.ratsim.nsl.modules.goaldecider.OneThenTheOtherGoalDecider;
import edu.usf.ratsim.nsl.modules.intention.Intention;
import edu.usf.ratsim.nsl.modules.intention.LastAteIntention;
import edu.usf.ratsim.nsl.modules.intention.NoIntention;

public class TSPModelKnownIDAC extends Model {

	// One action and 'intention' per feeder
	private int numActions = 5;
	private int numIntentions = 5;
	// Value table for actions and state-values (Actor Critic)
	private float[][] value;

	public TSPModelKnownIDAC() {
	}

	public TSPModelKnownIDAC(ElementWrapper params, Subject subject,
			LocalizableRobot lRobot) {
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

		LastTriedToEatGoalDecider lastTriedToEatGoalDecider = new LastTriedToEatGoalDecider(
				"Last Tried To Eat Goal Decider");
		addModule(lastTriedToEatGoalDecider);

		Module intention;
		if (numIntentions > 1) {
			intention = new LastAteIntention("Intention", numIntentions);
			intention.addInPort("goalFeeder",
					lastTriedToEatGoalDecider.getOutPort("goalFeeder"));
		} else {
			intention = new NoIntention("Intention", numIntentions);
		}
		addModule(intention);

		// Create the layers
		float radius = minPCRadius;
		LinkedList<RndConjCellLayer> conjCellLayers = new LinkedList<RndConjCellLayer>();
		List<Port> conjCellLayersPorts = new LinkedList<Port>();
		// For each layer
		for (int i = 0; i < numCCLayers; i++) {
			RndConjCellLayer ccl = new RndConjCellLayer("CCL " + i, lRobot,
					radius, 0, 0, numIntentions,
					numCCCellsPerLayer.get(i), "ExponentialPlaceIntentionCell", xmin, ymin, xmax,
					ymax, lRobot.getAllFeeders(), 0,
					layerLengths.get(i), 0);
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
		FloatMatrixPort valuePort = new FloatMatrixPort((Module) null, value);

		// Voting mechanism for action selection
		List<Port> votesPorts = new LinkedList<Port>();
		Module rlVotes = new GradientVotes("RL votes", numActions, connProbs,
				numCCCellsPerLayer, votesNormalizer);
		rlVotes.addInPort("states", stateCopy.getOutPort("copy"));
		rlVotes.addInPort("value", valuePort);
		addModule(rlVotes);

		// Add in port for dependency
		feederTraveler.addInPort("pc", placeCells.getOutPort("activation"));
		addModule(feederTraveler);

	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public void newEpisode() {
		// TODO Auto-generated method stub

	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
				.getOutPort("activation")).getNonZero());
		return activation;
	}

}
