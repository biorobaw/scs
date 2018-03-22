package edu.usf.ratsim.experiment.subject.morrisReplay;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TmazeRandomPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.multipleT.ClosestActionSelection;
import edu.usf.ratsim.nsl.modules.multipleT.MoveFromToActionPerformer;
import edu.usf.ratsim.nsl.modules.multipleT.NextActiveModule;
import edu.usf.ratsim.nsl.modules.multipleT.NextPositionModule;
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModuleAC;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class MorrisReplayModelAsleep extends MorrisReplayModel {

	private int numActions;
	private int numPCPerSide;
	private TmazeRandomPlaceCellLayer placeCells;
	public ProportionalVotes currentStateQ;
	public NextActiveModule nextActiveModule;
	
	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;
	
	public Reward rewardModule;
	
	public boolean[] visitedNodes;
	
	public SubjectAte subAte;
	
	public MorrisReplayModelAsleep() {
	}

	public MorrisReplayModelAsleep(ElementWrapper params, SubjectOld subject,
			LocalizableRobot lRobot, int numActions,int numPCPerSide,LinkedList<PlaceCell> pcList) {
		
		//Get parameters frorm xml file
		
		float discountFactor	= params.getChildFloat("discountFactor");
		float learningRate		= params.getChildFloat("learningRate");
		float foodReward		= params.getChildFloat("foodReward");
		
				
		this.numActions = numActions;
		this.numPCPerSide = numPCPerSide;
		
		
		
		
		
		
		//Model overview:
		
		/**
		 * Replay Model Johnson Redish 2005:
		 * 
		 * SLEEP STATE
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
		 * 
		 * subAte ---> reward--------------------|
		 * 										 |--->ActorCriticDeltaError------>UpdateQ
		 * pos ---> PCs---->currentStateQ (S)----|									/\							
		 * 																			|| (reverse dependency)
		 * NextActive--->getNextPos---------|------------------------------>getClosestAction
		 * 					/\				\/
		 * 					Pos--------------*-->NextActive----->getNextPos-
		 * 
		 * 
		 * 																				
		 * 
		 * NOTES:
		 * 		-The model is only a reference to understand the flow, modules do not correspond 1 to 1 with the model components
		 */
		
		
		//Create Variables Q,W, note sleepState has already been initialized.
		QTable = ((MorrisReplaySubject)subject).QTable;
		WTable = ((MorrisReplaySubject)subject).WTable;
		
		
		//INPUT AND STATE MODULES
		
		//create subAte module
		subAte = new SubjectAte("Subject Ate",subject);
		addModule(subAte);
		
		//Create reward module
		float nonFoodReward = 0;
		rewardModule = new Reward("foodReward", foodReward, nonFoodReward);
		rewardModule.addInPort("rewardingEvent", subAte.getOutPort("subAte")); 
		addModule(rewardModule);
		
		
		//Create pos module 
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		//Create Place Cells module
		placeCells = new TmazeRandomPlaceCellLayer("PCLayer",pcList);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		//Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ",numActions+1,true);
		currentStateQ.addInPort("states", placeCells.getOutPort("activation"));
		currentStateQ.addInPort("value", QTable);
		addModule(currentStateQ);
		
		
		//REPLAY ( ACTION SELECTION )		
		nextActiveModule = new NextActiveModule("nextActive",pcList);
		nextActiveModule.addInPort("W", WTable);
		addModule(nextActiveModule);
		
		
		//Create nextPosModule:
		//first get place cells center positions:
		LinkedList<Point3f> pcCenters = new LinkedList<Point3f>();
		for(PlaceCell pc : pcList){
			pcCenters.add(pc.getPreferredLocation());
		}
		
		Module nextPosModule = new NextPositionModule("nextPos",pcCenters);
		nextPosModule.addInPort("nextActive", nextActiveModule.getOutPort("nextActive"));
		addModule(nextPosModule);
		
		
		//Create action selection module -- choose action most similar to translation from one pc to the next
		Module actionSelection = new ClosestActionSelection("actionFromProbabilities",numActions);
		actionSelection.addInPort("position", pos.getOutPort("position"));
		actionSelection.addInPort("nextPosition", nextPosModule.getOutPort("nextPosition"));
		addModule(actionSelection);
		
		
		
		//PERFORM RL
		
		//Create deltaSignal module
		Module deltaError = new ActorCriticDeltaError("error", discountFactor, numActions);
		deltaError.addInPort("reward", rewardModule.getOutPort("reward"));
		deltaError.addInPort("Q",currentStateQ.getOutPort("votes"));
		addModule(deltaError);
		
		
		//Create update Q module
		Module updateQ = new UpdateQModuleAC("updateQ", numActions, learningRate);
		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
		updateQ.addInPort("action", actionSelection.getOutPort("action"),true);
		updateQ.addInPort("Q", QTable);
		updateQ.addInPort("placeCells", placeCells.getOutPort("activation"));
		addModule(updateQ);
		
		
		
		
		// MOVE ROBOT
		
		Module actionPerformer = new MoveFromToActionPerformer("actionPerformer",subject);
		actionPerformer.addInPort("position", pos.getOutPort("position"));
		actionPerformer.addInPort("nextPosition", nextPosModule.getOutPort("nextPosition"));
		addModule(actionPerformer);
//		actionPerformer.addPreReq(updateQ);
		
		
		
		
		
		//Add drawing utilities:
//		VirtUniverse universe = VirtUniverse.getInstance();
//		universe.addDrawingFunction(new DrawPolarGraph("Q softmax",50, 50, 50, softmax.probabilities,true));
//		
//		universe.addDrawingFunction(new DrawPolarGraph("gated probs",50, 170, 50, actionGating.probabilities,true));
//		universe.addDrawingFunction(new DrawPolarGraph("biased probs",50, 290, 50, biasModule.probabilities,true));
		
		
				

	}

	public void newTrial() {
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
		return ((Float0dPort)nextActiveModule.getOutPort("maxActivation")).get();
	}

	public boolean loopInReactivationPath(){
		return (boolean)Globals.getInstance().get("loopInReactivationPath");
	}

}
