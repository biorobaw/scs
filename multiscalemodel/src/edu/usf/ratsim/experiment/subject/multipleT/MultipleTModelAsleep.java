package edu.usf.ratsim.experiment.subject.multipleT;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Float1dCopyModule;
import edu.usf.micronsl.module.copy.Float1dSparseCopyModule;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
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
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModule;
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModuleAC;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;
import edu.usf.ratsim.nsl.modules.rl.SarsaQDeltaError;

public class MultipleTModelAsleep extends MultipleTModel {

	private int numActions;
	private int numPC;
	private TmazeRandomPlaceCellLayer placeCells;
	public ProportionalVotes currentStateQ;
	public NextActiveModule nextActiveModule;
	
	private float[][] QTable;
	private float[][] WTable;
	
	public boolean[] visitedNodes;
	
	public MultipleTModelAsleep() {
	}

	public MultipleTModelAsleep(ElementWrapper params, Subject subject,
			LocalizableRobot lRobot, int numActions,int numPC,LinkedList<PlaceCell> pcList) {
		
		//Get parameters frorm xml file
		
		float discountFactor	= params.getChildFloat("discountFactor");
		float learningRate		= params.getChildFloat("learningRate");
		float foodReward		= params.getChildFloat("foodReward");
		
				
		this.numActions = numActions;
		this.numPC = numPC;
		
		
		
		
		
		
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
		
		
		//Create Variables Q,W, note sleepState has already been initialized.
		QTable = ((MultipleTSubject)subject).QTable;
		FloatMatrixPort QPort = new FloatMatrixPort((Module) null, QTable);
		
		WTable = ((MultipleTSubject)subject).WTable;
		FloatMatrixPort WPort = new FloatMatrixPort((Module)null, WTable);
		
		
		//create subAte module
		SubjectAte subAte = new SubjectAte("Subject Ate",subject);
		addModule(subAte);
		
		//Create reward module
		float nonFoodReward = 0;
		Reward r = new Reward("foodReward", foodReward, nonFoodReward);
		r.addInPort("subAte", subAte.getOutPort("subAte"),true); // reward must execute before subAte (the reward obtained depends if we ate in the previous action)
		addModule(r);
		
		
		//Create pos module 
		Position pos = new Position("position", lRobot);
		addModule(pos);
		
		//Create Place Cells module
		placeCells = new TmazeRandomPlaceCellLayer("PCLayer",pcList);
		placeCells.addInPort("position", pos.getOutPort("position"));
		addModule(placeCells);
		
		//System.out.println("rad " + placeCells.getCells().get(0).getPlaceRadius());
		
		//Create PC copy module
		Float1dSparseCopyModule pcCopy = new Float1dSparseCopyModule("PCCopy");
		pcCopy.addInPort("toCopy",placeCells.getOutPort("activation"), true);
		addModule(pcCopy);
		
		
		//Create currentStateQ Q module
		currentStateQ = new ProportionalVotes("currentStateQ",numActions+1,true);
		currentStateQ.addInPort("states", placeCells.getOutPort("activation"));
		currentStateQ.addInPort("value", QPort);
		addModule(currentStateQ);
		
		//Create copy Q module
		Module copyQ = new Float1dCopyModule("copyQ");
		copyQ.addInPort("toCopy", currentStateQ.getOutPort("votes"),true);
		addModule(copyQ);
		
		
		//Create active and NextActive module:
		Module active = new Int0dCopyModule("active");
		addModule(active);
		
		nextActiveModule = new NextActiveModule("nextActive");
		nextActiveModule.addInPort("W", WPort);
		active.addInPort("toCopy", nextActiveModule.getOutPort("nextActive"),true);
		nextActiveModule.addInPort("active", active.getOutPort("copy"));
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
		
		
		//Create action selection module -- choose action according to probability distribution
		Module actionSelection = new ClosestActionSelection("actionFromProbabilities",numActions);
		actionSelection.addInPort("position", pos.getOutPort("position"));
		actionSelection.addInPort("nextPosition", nextPosModule.getOutPort("nextPosition"));
		addModule(actionSelection);
		
		
		//Create actionCopyModule
		Int0dCopyModule actionCopy = new Int0dCopyModule("actionCopy");
		actionCopy.addInPort("toCopy", actionSelection.getOutPort("action"),true);
		addModule(actionCopy);
		

		
		
		Module actionPerformer = new MoveFromToActionPerformer("actionPerformer",subject);
		actionPerformer.addInPort("position", pos.getOutPort("position"));
		actionPerformer.addInPort("nextPosition", nextPosModule.getOutPort("nextPosition"));
		addModule(actionPerformer);
		
		subAte.addPreReq(actionPerformer);
		
		
		//Create deltaSignal module
		Module deltaError = new ActorCriticDeltaError("error", discountFactor, numActions);
		deltaError.addInPort("reward", r.getOutPort("reward"));
		deltaError.addInPort("copyQ", copyQ.getOutPort("copy"));
		deltaError.addInPort("Q",currentStateQ.getOutPort("votes"));
		addModule(deltaError);
		
		
		//Create update Q module
		Module updateQ = new UpdateQModuleAC("updateQ", numActions, learningRate);
		updateQ.addInPort("delta", deltaError.getOutPort("delta"));
		updateQ.addInPort("action", actionCopy.getOutPort("copy"));
		updateQ.addInPort("Q", QPort);
		updateQ.addInPort("placeCells", pcCopy.getOutPort("copy"));
		addModule(updateQ);
		
		
		
		
		
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

	public void newEpisode() {
		// TODO Auto-generated method stub
		
		visitedNodes = new boolean[numPC];
		Globals.getInstance().put("loopInReactivationPath", false);
		
		getModule("PCLayer").getOutPort("activation").clear();
		getModule("PCCopy" ).getOutPort("copy").clear();
		//by doing this deltaQ(s_i,a_i) = nu*delta*State(s_i)*<a_i,a> = 0
				
		//set random placecell active and move robot to that position:
		int startingPlaceCell = RandomSingleton.getInstance().nextInt(numPC);
//		System.out.println("starting place cell: " + startingPlaceCell);
		visitedNodes[startingPlaceCell] = true;
		nextActiveModule.setVisitedArray(visitedNodes);
		((Int0dPort)nextActiveModule.getOutPort("nextActive")).set(startingPlaceCell);
		Point3f initPos = getPlaceCells().get(startingPlaceCell).getPreferredLocation();
		float theta = 0; //dont really care about head dir
		VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(initPos.x, initPos.y),theta);
		
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
