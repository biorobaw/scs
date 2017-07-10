package edu.usf.ratsim.model.taxi.discrete;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.model.PolicyModel;
import edu.usf.experiment.model.ValueModel;
import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.plot.float0d.Float0dSeriesPlot;
import edu.usf.micronsl.plot.float1d.Float1dBarPlot;
import edu.usf.micronsl.plot.float1d.Float1dDiscPlot;
import edu.usf.micronsl.plot.float1d.Float1dFillPlot;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.MaxAffordanceActionPerformer;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalValue;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.celllayer.DiscretePlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubFoundPlatform;
import edu.usf.ratsim.nsl.modules.multipleT.ActionGatingModule;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.ratsim.nsl.modules.rl.UpdateQModuleACTraces;

public class DiscreteTaxiModelAC extends Model implements ValueModel, PolicyModel {

	public DiscretePlaceCellLayer actionPlaceCells, valuePlaceCells;

	public ProportionalVotes currentStateQ;

	private FloatMatrixPort QTable;

	private Position pos;

	private ActorCriticDeltaError deltaError;

	private int gridSize;

	private int numActions;

	private UpdateQModuleACTraces updateQ;

	private ProportionalValue currentValue;

	private int numActionCells;

	private int numValueCells;

	private FloatMatrixPort VTable;

	private float foodReward;

	public DiscreteTaxiModelAC() {
	}

	public DiscreteTaxiModelAC(ElementWrapper params, Robot robot) {

		// Model parameters
		float discountFactor = params.getChildFloat("discountFactor");
		float learningRate = params.getChildFloat("learningRate");
		float tracesDecay = params.getChildFloat("tracesDecay");
		float minTrace = params.getChildFloat("minTrace");
		foodReward = params.getChildFloat("foodReward");
		float nonFoodReward = params.getChildFloat("nonFoodReward");
		List<Integer> actionValueSizes = params.getChildIntList("actionValueSizes");
		List<Integer> stateValueSizes = params.getChildIntList("stateValueSizes");
		boolean wallInteraction = params.getChildBoolean("wallInteraction");

		// Universe parameters
		gridSize = params.getChildInt("gridSize");

		LocalizableRobot lRobot = (LocalizableRobot) robot;
		AffordanceRobot affRobot = (AffordanceRobot) robot;

		numActions = affRobot.getPossibleAffordances().size();

		// Create pos module
		pos = new Position("position", lRobot);
		addModulePost(pos);

		List<DiscretePlaceCellLayer> pcLayers = new LinkedList<DiscretePlaceCellLayer>();

		// Create Place Cells module
		actionPlaceCells = new DiscretePlaceCellLayer("PCLayer Small", gridSize, gridSize, actionValueSizes,
				(GlobalWallRobot) robot, wallInteraction);
		actionPlaceCells.addInPort("position", pos.getOutPort("position"));
		addModulePost(actionPlaceCells);
		pcLayers.add(actionPlaceCells);

		valuePlaceCells = new DiscretePlaceCellLayer("PCLayer Large", gridSize, gridSize, stateValueSizes,
				(GlobalWallRobot) robot, wallInteraction);
		valuePlaceCells.addInPort("position", pos.getOutPort("position"));
		addModulePost(valuePlaceCells);
		pcLayers.add(valuePlaceCells);

		numActionCells = actionPlaceCells.getActivationPort().getSize();
		numValueCells = valuePlaceCells.getActivationPort().getSize();

		float[][] qvals = new float[numActionCells][numActions];
		this.QTable = new FloatMatrixPort(null, qvals);
		float[][] vVals = new float[numValueCells][1];
		this.VTable = new FloatMatrixPort(null, vVals);

		// Traces

		// Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ", numActions, foodReward);
		currentStateQ.addInPort("states", actionPlaceCells.getActivationPort());
		currentStateQ.addInPort("qValues", QTable);
		addModulePost(currentStateQ);
		DisplaySingleton.getDisplay().addPlot(new Float1dDiscPlot((Float1dPort) currentStateQ.getOutPort("votes"), "Action values"),
				0, 0, 1, 1);

		currentValue = new ProportionalValue("currentValueQ", foodReward);
		currentValue.addInPort("states", valuePlaceCells.getActivationPort());
		currentValue.addInPort("value", VTable);
		addModulePost(currentValue);
		DisplaySingleton.getDisplay()
				.addPlot(new Float0dSeriesPlot((Float0dPort) currentValue.getOutPort("value"), "State Value"), 0, 1, 1, 1);

		// Create SoftMax module
		Softmax softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes"));
		addModulePre(softmax);
		DisplaySingleton.getDisplay()
				.addPlot(new Float1dFillPlot((Float1dPort) softmax.getOutPort("probabilities"), "Softmax Probs"), 0, 2, 1, 1);

		// Create ActionGatingModule -- sets the probabilities of impossible
		// actions to 0 and then normalizes them
		ActionGatingModule actionGating = new ActionGatingModule("actionGating", robot);
		actionGating.addInPort("input", softmax.getOutPort("probabilities"));
		addModulePre(actionGating);
		DisplaySingleton.getDisplay()
				.addPlot(new Float1dBarPlot((Float1dPort) actionGating.getOutPort("probabilities"), "Action Gating"), 0, 3, 1, 1);

		// Create action selection module -- choose action according to
		// probability distribution
		Module actionSelection = new ActionFromProbabilities("actionFromProbabilities");
		actionSelection.addInPort("probabilities", actionGating.getOutPort("probabilities"));
		addModulePre(actionSelection);

		// Create Action Performer module
		MaxAffordanceActionPerformer actionPerformer = new MaxAffordanceActionPerformer("actionPerformer", robot);
		actionPerformer.addInPort("action", actionSelection.getOutPort("action"));
		addModulePre(actionPerformer);

		// // Cells are only computed after performing an action
		// placeCells.addPreReq(actionPerformer);

		// create subAte module
		SubFoundPlatform subFoundPlat = new SubFoundPlatform("Subject Found Plat", (PlatformRobot) robot);
		addModulePost(subFoundPlat);

		// Create reward module
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subFoundPlat.getOutPort("foundPlatform"));
		addModulePost(r);

		// Create deltaSignal module
		deltaError = new ActorCriticDeltaError("error", discountFactor, foodReward);
		deltaError.addInPort("reward", r.getOutPort("reward"));
		deltaError.addInPort("value", currentValue.getOutPort("value"));
		addModulePost(deltaError);

		// Create update Q module
		updateQ = new UpdateQModuleACTraces("updateQ", learningRate, tracesDecay, minTrace);
		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
		updateQ.addInPort("action", actionSelection.getOutPort("action"));
		updateQ.addInPort("Q", QTable);
		updateQ.addInPort("V", VTable);
		updateQ.addInPort("actionPlaceCells", actionPlaceCells.getActivationPort());
		updateQ.addInPort("valuePlaceCells", valuePlaceCells.getActivationPort());
		addModulePost(updateQ);

		DisplaySingleton.getDisplay().addUniverseDrawer(new QValueDrawer(this), 0);
		DisplaySingleton.getDisplay().addUniverseDrawer(new QPolicyDrawer(this, (AffordanceRobot) robot));
	}

	public void newEpisode() {
		super.newEpisode();
		// Compute place cell output before making the first decision
		pos.run();
		actionPlaceCells.run();
		valuePlaceCells.run();
		currentStateQ.run();
		currentValue.run();

		deltaError.saveValue();
		updateQ.savePCs();
	}

	@Override
	public Map<Coordinate, Float> getValuePoints() {
		Map<Coordinate, Float> valuePoints = new HashMap<Coordinate, Float>();

		ProportionalValue votes = new ProportionalValue("Logging Value", foodReward);

		for (int x = 0; x < gridSize; x++)
			for (int y = 0; y < gridSize; y++) {
				Coordinate pos = new Coordinate(x, y);
				Map<Integer, Float> activeMap = valuePlaceCells.getActive(pos);
				votes.run(activeMap, VTable);

				valuePoints.put(pos, votes.valuePort.get());
			}

		return valuePoints;
	}

	@Override
	public float getValueEntropy() {
		return 0;
	}

	public Map<Coordinate, Integer> getPolicyPoints() {
		Map<Coordinate, Integer> policyPoints = new HashMap<Coordinate, Integer>();

		ProportionalVotes votes = new ProportionalVotes("currentStateQ", numActions, foodReward);

		for (int x = 0; x < gridSize; x++)
			for (int y = 0; y < gridSize; y++) {
				Coordinate pos = new Coordinate(x, y);
				Map<Integer, Float> activeMap = actionPlaceCells.getActive(pos);
				votes.run(activeMap, QTable);

				float maxVal = -Float.MAX_VALUE;
				int maxAction = 0;
				int i = 0;
				for (int action = 0; action < numActions; action++) {
					float v = votes.actionVote[action];
					maxAction = v > maxVal ? i : maxAction;
					maxVal = v > maxVal ? v : maxVal;
					i++;
				}

				policyPoints.put(pos, maxAction);
			}

		return policyPoints;
	}

}
