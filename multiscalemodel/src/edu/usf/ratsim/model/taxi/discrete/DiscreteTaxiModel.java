package edu.usf.ratsim.model.taxi.discrete;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.MaxAffordanceActionPerformer;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.celllayer.DiscretePlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubFoundPlatform;
import edu.usf.ratsim.nsl.modules.multipleT.ActionGatingModule;
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModule;
import edu.usf.ratsim.nsl.modules.rl.QLDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class DiscreteTaxiModel extends Model {

	public DiscretePlaceCellLayer placeCells;

	public ProportionalVotes currentStateQ;

	private FloatMatrixPort QTable;

	private Position pos;

	private QLDeltaError deltaError;

	public DiscreteTaxiModel() {
	}

	public DiscreteTaxiModel(ElementWrapper params, Robot robot) {

		// Model parameters
		float discountFactor = params.getChildFloat("discountFactor");
		float learningRate = params.getChildFloat("learningRate");
		float foodReward = params.getChildFloat("foodReward");
		float nonFoodReward = params.getChildFloat("nonFoodReward");
		
		// Universe parameters
		int gridSize = params.getChildInt("gridSize");

		LocalizableRobot lRobot = (LocalizableRobot) robot;
		AffordanceRobot affRobot = (AffordanceRobot) robot;

		int numActions = affRobot.getPossibleAffordances().size();	
		
		// Create pos module
		pos = new Position("position", lRobot);
		addModule(pos);

		// Create Place Cells module
		placeCells = new DiscretePlaceCellLayer("PCLayer", gridSize, gridSize, "small");
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		float[][] qvals = new float[gridSize*gridSize][numActions];
		this.QTable = new FloatMatrixPort(null, qvals);

		// Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ", numActions, true);
		currentStateQ.addInPort("states", placeCells.getOutPort("output"));
		currentStateQ.addInPort("value", QTable);
		addModule(currentStateQ);

		// Create SoftMax module
		Softmax softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes"), true); 
		addModule(softmax);

		// Create ActionGatingModule -- sets the probabilities of impossible
		// actions to 0 and then normalizes them
		ActionGatingModule actionGating = new ActionGatingModule("actionGating", robot);
		actionGating.addInPort("input", softmax.getOutPort("probabilities"));
		addModule(actionGating);

		// Create action selection module -- choose action according to
		// probability distribution
		Module actionSelection = new ActionFromProbabilities("actionFromProbabilities");
		actionSelection.addInPort("probabilities", actionGating.getOutPort("probabilities"));
		addModule(actionSelection);

		// Create Action Performer module
		MaxAffordanceActionPerformer actionPerformer = new MaxAffordanceActionPerformer("actionPerformer", robot);
		actionPerformer.addInPort("action", actionSelection.getOutPort("action"));
		addModule(actionPerformer);
		
		// Cells are only computed after performing an action
		placeCells.addPreReq(actionPerformer);

		// create subAte module
		SubFoundPlatform subFoundPlat = new SubFoundPlatform("Subject Ate", robot);
		addModule(subFoundPlat);
		subFoundPlat.addPreReq(actionPerformer);

		// Create reward module
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subFoundPlat.getOutPort("foundPlatform"), true); 
		addModule(r);

		// Create deltaSignal module
		deltaError = new QLDeltaError("error", discountFactor);
		deltaError.addInPort("reward", r.getOutPort("reward"));
		deltaError.addInPort("Q", currentStateQ.getOutPort("votes"));
		deltaError.addInPort("action", actionSelection.getOutPort("action"));
		addModule(deltaError);

		// Create update Q module
		Module updateQ = new UpdateQModule("updateQ", learningRate);
		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
		updateQ.addInPort("action", actionSelection.getOutPort("action"));
		updateQ.addInPort("Q", QTable);
		updateQ.addInPort("placeCells", placeCells.getOutPort("output"));
		addModule(updateQ);

	}

	public void newEpisode() {
		// Compute place cell output before making the first decision
		pos.run();
		placeCells.run();
		currentStateQ.run();
		
		deltaError.saveQ();
	}

}
