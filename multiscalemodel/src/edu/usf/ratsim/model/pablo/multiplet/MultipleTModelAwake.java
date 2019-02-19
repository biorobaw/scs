package edu.usf.ratsim.model.pablo.multiplet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AbsoluteAngleAffordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.pablo.multiplet.submodules.DistanceAffordanceGatingModule;
import edu.usf.ratsim.model.pablo.multiplet.submodules.DistancesInputModule;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalValue;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TmazeRandomPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.multipleT.ActionGatingModule;
import edu.usf.ratsim.nsl.modules.multipleT.Last2ActionsActionGating;
import edu.usf.ratsim.nsl.modules.multipleT.MultipleTActionPerformer;
import edu.usf.ratsim.nsl.modules.multipleT.PlaceCellTransitionMatrixUpdater;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.ratsim.nsl.modules.rl.UpdateQModuleAC;
import edu.usf.ratsim.support.NotImplementedException;
import edu.usf.vlwsim.robot.VirtualRobot;
//import edu.usf.vlwsim.display.drawingUtilities.DrawPolarGraph;
import edu.usf.vlwsim.universe.VirtUniverse;

public class MultipleTModelAwake extends Model {

	public TmazeRandomPlaceCellLayer placeCells;

	public ProportionalVotes currentStateQ;
	private ProportionalValue currentValue;
	private ProportionalValue oldValue;

	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;
	private Float2dSparsePort VTable;
	
	private Module actionSelection;
	private AffordanceRobot affRobot;

	public Softmax softmax;

	public Last2ActionsActionGating twoActionsGateModule;
	public DistanceAffordanceGatingModule affordanceGateModule;

	private DistancesInputModule inputDisntaceModule;

	public MultipleTModelAwake() {
	}

	public MultipleTModelAwake(ElementWrapper params, Robot robot,int numActions, int numPC, Float2dSparsePort QTable, Float2dSparsePort VTable,
			Float2dSparsePort WTable, float step) {

		// Get parameters frorm xml file
		float PCRadius 		 = params.getChildFloat("PCRadius");
		String placeCellType = params.getChildText("placeCells");

		float discountFactor = params.getChildFloat("discountFactor");
		float learningRate 	 = params.getChildFloat("learningRate");
		float wTransitionLR  = params.getChildFloat("wTransitionLR");
		float foodReward 	 = params.getChildFloat("foodReward");
		float minDistance	 = params.getChildFloat("step");

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

		// ====================== VARIABLES ========================================
		
		// Create Variables Q,W, note sleepState has already been initialized.

		this.QTable = QTable;

		this.WTable = WTable;
		this.VTable = VTable;

		
		// ====================== INPUT MODULES ====================================
		
		// Create pos module
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		
		// create subAte module
		SubjectAte subAte = new SubjectAte("Subject Ate");
		addModule(subAte);
		
		
		//Create distance sensing module:
		float maxDistanceSensorDistance = 2f;
		inputDisntaceModule = new DistancesInputModule("distance input", (VirtualRobot)robot, numActions, maxDistanceSensorDistance);
		addModule(inputDisntaceModule);
		
		
		// ===================== current state ==================================
		
		// Create Place Cells module
		placeCells = new TmazeRandomPlaceCellLayer("PCLayer", PCRadius, numPC, placeCellType);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		
		
		
		// ===================== Learning Modules ===============================
		
		
		currentValue = new ProportionalValue("currentValueV", 10000);
		currentValue.addInPort("states", placeCells.getActivationPort());
		currentValue.addInPort("value", VTable);
		addModule(currentValue);	
		
		
		// Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subAte.getOutPort("subAte")); // reward
		addModule(r);																			// must
		
		
		
		// Create deltaSignal module		
		Module deltaError = new ActorCriticDeltaError("error", discountFactor, numActions);
		deltaError.addInPort("reward", r.getOutPort("reward"));
		deltaError.addInPort("newStateValue", currentValue.getOutPort("value"));
		deltaError.addInPort("wasActionOptimal", new Bool0dPort(null, true));
		addModule(deltaError);
		
		
		// Create update Q module
		//Module updateQ = new UpdateQModuleAC("updateQ", numActions, learningRate)
		Module updateQV = new UpdateQModuleAC("updateQ", learningRate);
		updateQV.addInPort("delta", deltaError.getOutPort("delta"));
		updateQV.addInPort("Q", QTable);
		updateQV.addInPort("V", VTable);
		updateQV.addInPort("actionPlaceCells", placeCells.getOutPort("activation"));
		updateQV.addInPort("valuePlaceCells", placeCells.getOutPort("activation"));
		addModule(updateQV);
		
		
		
		// ===================== Recalculation of V and Q =========================	
		
		//this calculation of V will be used on next cycle
		oldValue = new ProportionalValue("oldValueV", 10000);
		oldValue.addInPort("states", placeCells.getActivationPort());
		oldValue.addInPort("value", VTable);
		addModule(oldValue);	
		
		
		// Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ", numActions, 10000);
		currentStateQ.addInPort("states", placeCells.getActivationPort());
		currentStateQ.addInPort("qValues", QTable);
		addModule(currentStateQ);
		
		
		
		// ===================== Action Selection ===============================
		

		// Create SoftMax module
		softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes")); 
		addModule(softmax);


		// Create ActionGatingModule -- sets the probabilities of impossible
		// actions to 0 and then normalizes them
		affordanceGateModule = new DistanceAffordanceGatingModule("actionGating",numActions,minDistance);
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

		
		// ===================== W Related Modules ===============================
		
		// Create UpdateW module
		PlaceCellTransitionMatrixUpdater wUpdater = new PlaceCellTransitionMatrixUpdater("wUpdater", numPC,wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("wPort", WTable);
		addModule(wUpdater);
		
		
		
		// ===================== Backward connection =============================

		// Add extra input to bias Module
		twoActionsGateModule.addInPort("action", actionSelection.getOutPort("action"),true);
		deltaError.addInPort("oldStateValue", oldValue.getOutPort("value"),true);
		updateQV.addInPort("action", actionSelection.getOutPort("action"),true);
		

	}

	public void newTrial() {
		getModule("PCLayer").getOutPort("activation").clear();
		// by doing this deltaQ(s_i,a_i) = nu*delta*State(s_i)*<a_i,a> = 0

		((PlaceCellTransitionMatrixUpdater) getModule("wUpdater")).newTrial();

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

}
