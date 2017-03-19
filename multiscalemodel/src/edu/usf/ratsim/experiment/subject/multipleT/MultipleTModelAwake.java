package edu.usf.ratsim.experiment.subject.multipleT;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPolarGraph;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
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
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModuleAC;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class MultipleTModelAwake extends MultipleTModel {

	public TmazeRandomPlaceCellLayer placeCells;
	
	public ProportionalVotes currentStateQ;
	public SubjectAte subAte;
	
	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;

	public MultipleTModelAwake() {
	}

	public MultipleTModelAwake(ElementWrapper params, SubjectOld subject,
			LocalizableRobot lRobot, int numActions,int numPC) {
		
		//Get parameters frorm xml file
		float PCRadius 			= params.getChildFloat("PCRadius");
		String placeCellType 	= params.getChildText("placeCells");
		
		float discountFactor	= params.getChildFloat("discountFactor");
		float learningRate		= params.getChildFloat("learningRate");
		float wTransitionLR		= params.getChildFloat("wTransitionLR");
		float foodReward		= params.getChildFloat("foodReward");
		
		float sameActionBias 	= params.getChildFloat("sameActionBias");
		float maxDistanceSensorDistnace = params.getChildFloat("maxDistanceSensorDistance");
		
		
		
		//Model overview:
		
		/**
		 * Replay Model Johnson Redish 2005:
		 * 
		 * Variables:
		 * 		Q 			Table
		 * 		W 			Transition Table
		 *      sleepState 	State: AWAKE/SLEEPING
		 * MODEL:
		 * Choose random cell
		 * 
		 * 
		 * activate cell
		 * propagate activity
		 * 
		 * 																									              
		 * subAte ---> reward--------------------|
		 * 										 |--->ActorCriticDeltaError------>UpdateQ
		 * pos ---> PCs---->currentStateQ (S)----|									/\
		 * 																			|| (reverse dependency)
		 * S-->Softmax-->actionGating--->last2ActionGating--->ActionSelection-------||
		 * 
		 * 		
		 * PCs ---> updateW
		 * 
		 * 
		 * NOTES:
		 * 		-The model is only a reference to understand the flow, modules do not correspond 1 to 1 with the model components
		 * 		-actionGating checks weather an action can be performed or not before action selection
		 * 		-last 2 action gating gives bias towards maintaining current movement
		 */
		
		
		//Create Variables Q,W, note sleepState has already been initialized.

		QTable = ((MultipleTSubject)subject).QTable;
		WTable = ((MultipleTSubject)subject).WTable;
		
		
		//INPUTS TO THE CYCLE
		//Create pos module 
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		//create subAte module
		subAte = new SubjectAte("Subject Ate",subject);
		addModule(subAte);
		
		
		
		//PROCESS INPUTS TO GET BASIC VAIABLES (PC, REWARDS, current state, etc)
		//Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subAte.getOutPort("subAte")); 
		addModule(r);
		
		
		//Create Place Cells module
		placeCells = new TmazeRandomPlaceCellLayer("PCLayer", PCRadius, numPC, placeCellType);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		
		//Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ",numActions+1,true);
		currentStateQ.addInPort("states", placeCells.getOutPort("activation"));
		currentStateQ.addInPort("value", QTable);
		addModule(currentStateQ);
		
		
		//DO ACTION SELECTION USING LAST CYCLE VALUE TABLE -- avoids doing PC * V twice per cycle 
		
		//Create SoftMax module
		Softmax softmax = new Softmax("softmax", numActions);
		softmax.addInPort("input", currentStateQ.getOutPort("votes")); // executes with last estimation of Q
		addModule(softmax);
		
		
		//create sameActionBias module:
		//Assigns bias chance of choosing previous action and (1-bias) of using current probabilities
		//must be done before deleting impossible actions otherwise if the old action is 
		//now impossible it will have 50% chance to be taken again
		//CurrentActionBiasModule biasModule = new CurrentActionBiasModule("bias",numActions,sameActionBias);
		//biasModule.addInPort("input", softmax.getOutPort("probabilities")); 
		//addModule(biasModule);
		//need to add old action input created later
		
		
		//Create ActionGatingModule -- sets the probabilities of impossible actions to 0 and then normalizes them
		ActionGatingModule actionGating = new ActionGatingModule("actionGating", numActions, ((MultipleTSubject)subject).step,maxDistanceSensorDistnace);
		actionGating.addInPort("input", softmax.getOutPort("probabilities"));
		addModule(actionGating);
		
		
		//Add bias module to probabilities
		//DontGoBackBiasModule biasModule = new DontGoBackBiasModule("bias", numActions, 7, 0.01f);
		Last2ActionsActionGating biasModule = new Last2ActionsActionGating("bias", numActions, 1.5f, 0.001f);
		biasModule.addInPort("input", actionGating.getOutPort("probabilities"));
		addModule(biasModule);
		
		
		//Create action selection module -- choose action according to probability distribution
		Module actionSelection = new ActionFromProbabilities("actionFromProbabilities");
		actionSelection.addInPort("probabilities", biasModule.getOutPort("probabilities"));
		addModule(actionSelection);
		
		//Add extra input to bias Module
		biasModule.addInPort("action", actionSelection.getOutPort("action"),true);
		
		
		
		//DO REINFORCEMENT LEARNING
		//Create deltaSignal module
		Module deltaError = new ActorCriticDeltaError("error", discountFactor, numActions);
		deltaError.addInPort("reward", r.getOutPort("reward"));
		deltaError.addInPort("Q",currentStateQ.getOutPort("votes"));
		addModule(deltaError);
		
		
		//update Q module
		Module updateQ = new UpdateQModuleAC("updateQ", numActions, learningRate);
		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
		updateQ.addInPort("action", actionSelection.getOutPort("action"),true);
		updateQ.addInPort("Q", QTable);
		updateQ.addInPort("placeCells", placeCells.getOutPort("activation"));
		addModule(updateQ);
		
		
		//UPDATE PATH MATRIX
		PlaceCellTransitionMatrixUpdater wUpdater = new PlaceCellTransitionMatrixUpdater("wUpdater", numPC, wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("wPort", WTable);
		addModule(wUpdater);
		
		
		
		
		//PERFORM ACTION
		
		//Create Action Performer module
		MultipleTActionPerformer actionPerformer = new MultipleTActionPerformer("actionPerformer", numActions, ((MultipleTSubject)subject).step, subject);
		actionPerformer.addInPort("action", actionSelection.getOutPort("action"));
		addModule(actionPerformer);
		

		
		
		
		
		
		
		
		//ADD DRAWING UTILITIES:
		VirtUniverse universe = VirtUniverse.getInstance();
		universe.addDrawingFunction(new DrawPolarGraph("Q softmax",50, 50, 50, softmax.probabilities,true));
		
		universe.addDrawingFunction(new DrawPolarGraph("gated probs",50, 170, 50, actionGating.probabilities,true));
		universe.addDrawingFunction(new DrawPolarGraph("biased probs",50, 290, 50, biasModule.probabilities,true));
		
		universe.addDrawingFunction(new DrawPolarGraph("bias ring",50, 410, 50, biasModule.chosenRing,true));
		
				
		

	}

	public void newTrial() {
		getModule("PCLayer").getOutPort("activation").clear();
		//by doing this deltaQ(s_i,a_i) = nu*delta*State(s_i)*<a_i,a> = 0
		
		((PlaceCellTransitionMatrixUpdater)getModule("wUpdater")).newTrial();
		
		
		//need to let the bias module know that a new episode started (do not bias on fisrt turn)
		((Last2ActionsActionGating)getModule("bias")).newTrial();
		
		
		
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
					.getOutPort("activation")).getNonZero());
		return activation;
	}
	
	public float getMaxActivation(){
		throw new NotImplementedException();
	}



}
