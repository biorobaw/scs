package edu.usf.ratsim.model.pablo.morris_replay;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AbsoluteAngleAffordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.AreEqualModule;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.MaxModule;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.ProportionalValueSingleBlockMatrix;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.ProportionalVotesSingleBlockMatrix;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.UpdateQModuleAC2;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.VQErrorSignalModule;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.WUpdater2;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.WallGateModule;
import edu.usf.ratsim.model.pablo.multiplet.submodules.DistanceAffordanceGatingModule;
import edu.usf.ratsim.model.pablo.multiplet.submodules.DistancesInputModule;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.multipleT.Last2ActionsActionGating;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.ratsim.support.NotImplementedException;
import edu.usf.vlwsim.robot.VirtualRobot;

public class ModelAwake extends Model {

//	public TmazeRandomPlaceCellLayer placeCells;
	public TesselatedPlaceCellLayer placeCells;


	public ProportionalVotesSingleBlockMatrix currentStateQ;
	private ProportionalValueSingleBlockMatrix newStateValue;
	private ProportionalValueSingleBlockMatrix oldStateValue;

	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;
	private Float2dSparsePort VTable;
	
	private Module actionSelection;
	private AffordanceRobot affRobot;

	public Softmax softmax;

	public Last2ActionsActionGating twoActionsGateModule;
	public WallGateModule affordanceGateModule;

	private DistancesInputModule inputDisntaceModule;


	private String placeCellType;



	public ModelAwake() {
	}

	public ModelAwake(ElementWrapper params, Robot robot,int numActions, int numPC, Float2dSparsePort QTable, Float2dSparsePort VTable,
			Float2dSparsePort WTable, float step) {

		// Get parameters frorm xml file
		float PCRadius 		 = params.getChildFloat("PCRadius");
		placeCellType = params.getChildText("placeCells");

		float discountFactor = params.getChildFloat("discountFactor");
		float learningRate 	 = params.getChildFloat("learningRate");
		float wTransitionLR  = params.getChildFloat("wTransitionLR");
		float foodReward 	 = params.getChildFloat("foodReward");
		float minDistance	 = params.getChildFloat("step")*1.1f;

		float sameActionBias = params.getChildFloat("sameActionBias");
		//float maxDistanceSensorDistnace = params.getChildFloat("maxDistanceSensorDistance");

		LocalizableRobot lRobot = (LocalizableRobot) robot;
		affRobot = (AffordanceRobot) robot;

		
		// Model overview:numActions

		/**
		 * Replay Model: original version Johnson Redish 2005:
		 * 
		 * Variables: Q Table W Transition Table sleepState State:
		 * AWAKE/SLEEPING MODEL: Choose random cell
		 * 
		 * 
		 * activate cell propagate activity
		 * 
		 * Copy of Action and pcCopy \/
		 * Reward-------------------------------->*---->*------------------------------------------->*--------->*->deltaSignal----->UpdateQ
		 * /\ /\ /\ /\ | | | | PCCopy--*-> UpdateW Qcopy | ActionCopy | | / | |
		 * | | \/ / \/ | \/ | Pos---->
		 * PlaceCells---------------->currentStateQ-->SoftMax-->ActionGating-->bias------>ActionSelection----->ActionPerformer--->subAte
		 * 
		 * 
		 * NOTES: -The model is only a reference to understand the flow, modules
		 * do not correspond 1 to 1 with the model components -subAte =
		 * subjectAte (already existing module) -backDep = backward dependency
		 * -actionGating checks weather an action can be performed or not before
		 * action selection -UpdateQ requires Qcopy and actionCopy or
		 * currentStateQ and -Reward receives input from subAte but executes
		 * before
		 */

		
		//======= VARIABLES ================================================
		
		// Create Variables Q,W, note sleepState has already been initialized.
		this.QTable = QTable;
		this.VTable = VTable;
		this.WTable = WTable;

		
		//======= INPUT MODULES =============================================
		// Create pos module
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		
		// create subAte module
		SubjectAte subAte = new SubjectAte("Subject Ate", robot);
		addModule(subAte);
		
					
		//Create distance sensing module - calculates distances to all walls:
		float maxDistanceSensorDistance = 2f;
		inputDisntaceModule = new DistancesInputModule("distance input", (VirtualRobot)robot, numActions, maxDistanceSensorDistance);
		addModule(inputDisntaceModule);
		
		
		//======= CURRENT STATE ==============================================
		
		// Create Place Cells module
//		placeCells = new TmazeRandomPlaceCellLayer("PCLayer", PCRadius, numPC, placeCellType);
		placeCells = new TesselatedPlaceCellLayer("PCLayer", PCRadius, (int)Math.sqrt(numPC), placeCellType, -1f, -1f, 1f, 1f);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
				
		//======= LEARNING MODULES ===========================================
		
		
		//Calculate the value of s_t using \{v^{t-1}_i\}
		newStateValue = new ProportionalValueSingleBlockMatrix("newStateValue");
		newStateValue.addInPort("states", placeCells.getActivationPort());
		newStateValue.addInPort("value", VTable);
		addModule(newStateValue);
		
		
		// Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subAte.getOutPort("subAte")); // reward
		addModule(r);																			// must
		
		
		// Create error signal module for V
		Module error = new VQErrorSignalModule("error", discountFactor);
		error.addInPort("reward", r.getOutPort("reward"));
		error.addInPort("newStateValue", newStateValue.getOutPort("value"));
		addModule(error);
		
		
		// Create update Q module
		//Module updateQ = new UpdateQModuleAC("updateQ", numActions, learningRate)
		Module updateQV = new UpdateQModuleAC2("updateQ", learningRate);
		updateQV.addInPort("deltaV", error.getOutPort("deltaV"));
		updateQV.addInPort("deltaQ", error.getOutPort("deltaQ"));
		updateQV.addInPort("Q", QTable);
		updateQV.addInPort("V", VTable);
		updateQV.addInPort("pcs", placeCells.getOutPort("activation"));
		addModule(updateQV);
		
		
		
		
		//======= RECALCULATION OF V AND Q AFTER UPDATE ======================
		
		
		// Create currentStateQ Q module
		currentStateQ = new ProportionalVotesSingleBlockMatrix("currentStateQ", numActions);
		currentStateQ.addInPort("states", placeCells.getActivationPort());
		currentStateQ.addInPort("qValues", QTable);
		currentStateQ.addPreReq(updateQV);
		addModule(currentStateQ);
		
		
		//calculate next iteration "oldStateValue"
		oldStateValue = new ProportionalValueSingleBlockMatrix("oldStateValue");
		oldStateValue.addInPort("states", placeCells.getActivationPort());
		oldStateValue.addInPort("value", VTable);
		oldStateValue.addPreReq(updateQV);
		addModule(oldStateValue);
		
		
		//======= ACTION SELECTION MODULES ===================================
		

		// Create SoftMax module
		softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes")); 
		addModule(softmax);

		// create sameActionBias module:
		// Assigns bias chance of choosing previous action and (1-bias) of using
		// current probabilities
		// must be done before deleting impossible actions otherwise if the old
		// action is
		// now impossible it will have 50% chance to be taken again
		// CurrentActionBiasModule biasModule = new
		// CurrentActionBiasModule("bias",numActions,sameActionBias);
		// biasModule.addInPort("input", softmax.getOutPort("probabilities"));
		// addModule(biasModule);
		// need to add old action input created later

		// Create ActionGatingModule -- sets the probabilities of impossible
		// actions to 0 and then normalizes them
		affordanceGateModule = new WallGateModule("actionGating",numActions,minDistance);
		affordanceGateModule.addInPort("input", softmax.getOutPort("probabilities"));
		affordanceGateModule.addInPort("distances", inputDisntaceModule.getOutPort("distances"));
		addModule(affordanceGateModule);

		// Add bias module to probabilities
		// DontGoBackBiasModule biasModule = new DontGoBackBiasModule("bias",
		// numActions, 7, 0.01f);
		twoActionsGateModule = new Last2ActionsActionGating("bias", numActions, 1.5f, 0.001f);
		twoActionsGateModule.addInPort("input", affordanceGateModule.getOutPort("probabilities"));
		addModule(twoActionsGateModule);

		// Create action selection module -- choose action according to
		// probability distribution
		actionSelection = new ActionFromProbabilities("actionFromProbabilities");
		actionSelection.addInPort("probabilities", twoActionsGateModule.getOutPort("probabilities"));
		addModule(actionSelection);


		//Check weather action selection is optimal value:
		//first find value of max action
//		MaxModule maxModule =  new MaxModule("maxModule");
//		maxModule.addInPort("values", currentStateQ.getOutPort("votes"));
//		addModule(maxModule);
//		
//		//then check if value of selected action is equal to value of max action
//		AreEqualModule areEqualModule = new AreEqualModule("areEqual", 0.00001f);
//		areEqualModule.addInPort("values", currentStateQ.getOutPort("votes"));
//		areEqualModule.addInPort("input1", maxModule.maxVal);
//		areEqualModule.addInPort("input2", actionSelection.getOutPort("action"));
//		addModule(areEqualModule);
		
		
		//======= W RELATED MODULES ==========================================
		
		// Create UpdateW module
		WUpdater2 wUpdater = new WUpdater2("wUpdater", numPC,wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("wPort", WTable);
		addModule(wUpdater);
		
		
		//======= ADD ALL BACKWARD DEPENDENCIES ==============================
		
		
		// Add extra input to bias Module
		twoActionsGateModule.addInPort("action", actionSelection.getOutPort("action"),true);
		
		//indicate to the error function whether the previous action was optimal
		//errorV.addInPort("wasActionOptimal", areEqualModule.areEqual,true);
		//provide old stateValue to error signal
		error.addInPort("oldStateValue", oldStateValue.getOutPort("value"),true);
		error.addInPort("oldActionValues", currentStateQ.getOutPort("votes"),true);
		error.addInPort("action",actionSelection.getOutPort("action"),true);
		
		//add action to module that updates Q:
		updateQV.addInPort("action", actionSelection.getOutPort("action"),true);
		
		
	}

	public void newTrial() {
		getModule("PCLayer").getOutPort("activation").clear();
		// by doing this deltaQ(s_i,a_i) = nu*delta*State(s_i)*<a_i,a> = 0

		((WUpdater2) getModule("wUpdater")).newTrial();

		// need to let the bias module know that a new episode started (do not
		// bias on fisrt turn)
		((Last2ActionsActionGating) getModule("bias")).newTrial();

	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells.getOutPort("activation")).getNonZero());
		return activation;
	}

	public float getMaxActivation() {
		throw new NotImplementedException();
	}
	
	
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
	}
	
	
	public int chosenAction = -1;
	@Override
	public void finalTask() {
		// TODO Auto-generated method stub
		super.finalTask();
		chosenAction = ((Int0dPort)actionSelection.getOutPort("action")).get();
		AbsoluteAngleAffordance aff = (AbsoluteAngleAffordance)affRobot.getPossibleAffordances().get(chosenAction);
		affRobot.executeAffordance(aff);
		affRobot.executeAffordance(new EatAffordance());
		
		
	}

	public void setPCcenters(float[][] c) {
		// TODO Auto-generated method stub
		placeCells.setPCs(c);
		
	}

}
