package edu.usf.ratsim.model.pablo.morris_replay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.AreEqualModule;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.MaxModule;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.ProportionalValueSingleBlockMatrix;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.ProportionalVotesSingleBlockMatrix;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.UpdateQModuleAC2;
import edu.usf.ratsim.model.pablo.morris_replay.submodules.VQErrorSignalModule;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalValue;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.celllayer.TmazeRandomPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectFoundFood;
import edu.usf.ratsim.nsl.modules.multipleT.ClosestActionSelection;
import edu.usf.ratsim.nsl.modules.multipleT.MoveFromToActionPerformer;
import edu.usf.ratsim.nsl.modules.multipleT.NextActiveModule;
import edu.usf.ratsim.nsl.modules.multipleT.NextPositionModule;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.ratsim.nsl.modules.rl.UpdateQModuleAC;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

public class ModelAsleep extends Model {

	private int numActions;
	private int numPC;
//	public TmazeRandomPlaceCellLayer placeCells;
	public TesselatedPlaceCellLayer placeCells;
	public ProportionalVotesSingleBlockMatrix currentStateQ;
	private ProportionalValueSingleBlockMatrix newStateValue;
	public NextActiveModule nextActiveModule;
	private NextPositionModule nextPosModule;
	
	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;
	private Float2dSparsePort VTable;
	
	public boolean[] visitedNodes;
	
	private Robot robot;
	private VQErrorSignalModule error;
	private UpdateQModuleAC2 updateQV;
	
	public boolean doneReplaying = false;
	private SubjectFoundFood subFoundFood;
	private Module oldStateValue;
	
	public ModelAsleep() {
	}

	public ModelAsleep(ElementWrapper params,
			Robot robot, int numActions,int numPC,ArrayList<PlaceCell> pcList, Float2dSparsePort QTable, Float2dSparsePort VTable,
			Float2dSparsePort WTable, float step) {
		
		//Get parameters frorm xml file
		
		float discountFactor	= params.getChildFloat("discountFactor");
		float learningRate		= params.getChildFloat("learningRate");
		float foodReward		= params.getChildFloat("foodReward");
		float propagationThreshold = params.getChildFloat("replayThres");
		String pcType 			   = params.getChildText("placeCells");
		float PCRadius 		 	   = params.getChildFloat("PCRadius");
		
				
		this.numActions = numActions;
		this.numPC = numPC;
		
		LocalizableRobot lRobot = (LocalizableRobot) robot;
		this.robot = robot;
		
		
		
		
		
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
		 * 																					              Copy of Action and placeCells
		 * 																								                \/
		 * Reward-------------------->*---->*------------------------------------------->*--------->*->deltaSignal----->UpdateQ
		 * 			                 /\    /\                              	  			/\         /\
		 *                            |     |                                			 |          |
		 * 	          PCCopy		Qcopy 	|							  			  ActionCopy    |
		 *				 |			  |		|					  			 			 |			|	 
		 * 				\/			 \/		|					  		 				 |          |
		 * Pos---->	PlaceCells--->currentStateQ 										 |			|
		 * 	  																			 |			|
		 * 																				 |			|
		 * 																				\/		   	|
		 * Active , W ---------->NextActive----->getNextPos--------------------------->getClosestAction
		 * 												   *------>MoveFromToPosActionPerformer----/\----->subAte
		 * 																				   /\	  	|
		 *																					|		| 
		 * 																					Pos	----*	
		 * 
		 * NOTES:
		 * 		-The model is only a reference to understand the flow, modules do not correspond 1 to 1 with the model components
		 * 		-subAte = subjectAte (already existing module)
		 * 		-backDep = backward dependency
		 * 		-actionGating checks weather an action can be performed or not before action selection
		 * 		-UpdateQ requires Qcopy and actionCopy or currentStateQ and 
		 * 		-Reward receives input from subAte but executes before
		 */
		
		
		//======= VARIABLES ================================================
		
		//Create Variables Q,W, note sleepState has already been initialized.
		this.QTable = QTable;
		this.VTable = VTable;
		this.WTable = WTable;
		
		
		//======= INPUT MODULES =============================================
		//Create pos module 
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		
		subFoundFood = new SubjectFoundFood("Subject Found Food", robot);
		addModule(subFoundFood);
	
		
		//======= CURRENT STATE ==============================================
		
		//Create Place Cells module
//		placeCells = new TmazeRandomPlaceCellLayer("PCLayer",pcList);
		
		placeCells = new TesselatedPlaceCellLayer("PCLayer", PCRadius, (int)Math.sqrt(numPC), pcType, -1f, -1f, 1f, 1f);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		
		//======= LEARNING MODULES ===========================================

		//Calculate the value of s_t using \{v^{t-1}_i\}
		newStateValue = new ProportionalValueSingleBlockMatrix("newStateValue");
		newStateValue.addInPort("states", placeCells.getActivationPort());
		newStateValue.addInPort("value", VTable);
		addModule(newStateValue);
		
		//Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("rewardingEvent", subFoundFood.getOutPort("subFoundFood")); 
		addModule(r);
		
		//Create deltaSignal module
		error = new VQErrorSignalModule("error", discountFactor);
		error.addInPort("reward", r.getOutPort("reward"));
		error.addInPort("newStateValue", newStateValue.getOutPort("value"));
		addModule(error);
		
		
		//Create update Q module		
		updateQV = new UpdateQModuleAC2("updateQ", learningRate);
		updateQV.addInPort("deltaV", error.getOutPort("deltaV"));
		updateQV.addInPort("deltaQ", error.getOutPort("deltaQ"));
		updateQV.addInPort("Q", QTable);
		updateQV.addInPort("V", VTable);
		updateQV.addInPort("pcs", placeCells.getActivationPort() );
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
		
		//Given W, find out next active PC
		nextActiveModule = new NextActiveModule("nextActive",propagationThreshold);
		nextActiveModule.addInPort("W", WTable);
		addModule(nextActiveModule);
		

		//Create module that associates PCs to locations
		//first get place cells center positions:
		LinkedList<Coordinate> pcCenters = new LinkedList<Coordinate>();
		for(PlaceCell pc : pcList){
			pcCenters.add(pc.getPreferredLocation());
		}

		//then create the module
		nextPosModule = new NextPositionModule("nextPos",pcCenters);
		nextPosModule.addInPort("nextActive", nextActiveModule.getOutPort("nextActive"));
		addModule(nextPosModule);
		
		
		//Create action selection module:
		//choose closest action that takes rat from current pos to next
		Module actionSelection = new ClosestActionSelection("actionFromProbabilities",numActions);
		actionSelection.addInPort("position", pos.getOutPort("position"));
		actionSelection.addInPort("nextPosition", nextPosModule.getOutPort("nextPosition"));
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
//		
		
		
		
		//======= ADD ALL BACKWARD DEPENDENCIES ==============================
		
		//provide old stateValue to error signal
		error.addInPort("oldStateValue", oldStateValue.getOutPort("value"),true);
		error.addInPort("oldActionValues", currentStateQ.getOutPort("votes"),true);
		error.addInPort("action",actionSelection.getOutPort("action"),true);
				
		//add action to module that updates Q:
		updateQV.addInPort("action", actionSelection.getOutPort("action"),true);
		
		

		
		
				

	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		visitedNodes = new boolean[numPC];
		doneReplaying = false;
				
		//set random placecell active and move robot to that position:
		int startingPlaceCell = RandomSingleton.getInstance().nextInt(numPC);
//		System.out.println("starting place cell: " + startingPlaceCell);
		visitedNodes[startingPlaceCell] = true;
		nextActiveModule.setVisitedArray(visitedNodes);
		((Int0dPort)nextActiveModule.getOutPort("nextActive")).set(startingPlaceCell);
		Coordinate initPos = getPlaceCells().get(startingPlaceCell).getPreferredLocation();
		VirtUniverse.getInstance().setRobotPosition(initPos);
		

		
	}

	public Map<Integer, Float> getCellActivation() {
//		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
//		activation.putAll(((Float1dSparsePortMap) placeCells
//					.getOutPort("activation")).getNonZero());
//		return activation;
		return null;
	}

//	public float getMaxActivation(){
//		return ((Float0dPort)nextActiveModule.getOutPort("maxActivation")).get();
//	}

	public boolean doneReplaying(){
//		if(subFoundFood.outPort.get()) System.out.println("replay finished finding food");
//		else if (!nextActiveModule.propagate) System.out.println("replay finished - unable to propagate");
		return subFoundFood.outPort.get() || !nextActiveModule.propagate;
	}
	
	
	
	Coordinate nextPos = null;
	
	@Override
	public void finalTask() {
		// TODO Auto-generated method stub
		super.finalTask();

		
		nextPos = ((PointPort)nextPosModule.getOutPort("nextPosition")).get();
		

		
	}
	
	public void move(){
		((TeleportRobot)robot).setPosition(nextPos);
	}

	public void setPCcenters(float centers[][]){
		nextPosModule.setPositions(centers);
		placeCells.setPCs(centers);
	}
	
}
