package edu.usf.ratsim.experiment.subject.morrisReplay;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.module.copy.Int0dCopyModule;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.experiment.universe.virtual.CanvasRecorder;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawCycleInformation;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPolarGraph;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.actionselection.ProportionalVotes;
import edu.usf.ratsim.nsl.modules.actionselection.Softmax;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.multipleT.ActionGatingModule;
import edu.usf.ratsim.nsl.modules.multipleT.Last2ActionsActionGating;
import edu.usf.ratsim.nsl.modules.multipleT.PlaceCellTransitionMatrixUpdater;
import edu.usf.ratsim.nsl.modules.multipleT.UpdateQModuleAC;
import edu.usf.ratsim.nsl.modules.rl.ActorCriticDeltaError;
import edu.usf.ratsim.nsl.modules.rl.Reward;

public class MorrisReplayModelAwake extends MorrisReplayModel {

	public TesselatedPlaceCellLayer placeCells;
	public ActionFromProbabilities actionSelection;
	
	public ProportionalVotes currentStateQ;
	public SubjectAte subAte;
	
	private Float2dSparsePort QTable;
	private Float2dSparsePort WTable;
	
	public Reward rewardModule;
	
	
	public MorrisReplaySubject subject;
	
	
	
	float angles[];
	int numActions;

	public MorrisReplayModelAwake() {
	}

	public MorrisReplayModelAwake(ElementWrapper params, SubjectOld subject,
			LocalizableRobot lRobot, int numActions,int numPCPerSide) {
		
		this.subject = (MorrisReplaySubject)subject;
		this.numActions = numActions;
		
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

		QTable = ((MorrisReplaySubject)subject).QTable;
		WTable = ((MorrisReplaySubject)subject).WTable;
		
		
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
		rewardModule = new Reward("foodReward", foodReward, nonFoodReward);
		rewardModule.addInPort("rewardingEvent", subAte.getOutPort("subAte")); 
		addModule(rewardModule);
		
		
		//Create Place Cells module
//		placeCells = new TmazeRandomPlaceCellLayer("PCLayer", PCRadius, numPCPerSide, placeCellType);
		float poolRadius = 0.75f;
		placeCells = new TesselatedPlaceCellLayer("PCLayer", PCRadius, numPCPerSide, placeCellType, -poolRadius, -poolRadius, poolRadius, poolRadius);
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
		ActionGatingModule actionGating = new ActionGatingModule("actionGating", numActions, ((MorrisReplaySubject)subject).step,maxDistanceSensorDistnace);
		actionGating.addInPort("input", softmax.getOutPort("probabilities"));
		addModule(actionGating);
		
		
		//Add bias module to probabilities
		//DontGoBackBiasModule biasModule = new DontGoBackBiasModule("bias", numActions, 7, 0.01f);
		Last2ActionsActionGating biasModule = new Last2ActionsActionGating("bias", numActions, 1.5f, 0.001f);
		biasModule.addInPort("input", actionGating.getOutPort("probabilities"));
		addModule(biasModule);
		
		
		//Create action selection module -- choose action according to probability distribution
		actionSelection = new ActionFromProbabilities("actionFromProbabilities");
		actionSelection.addInPort("probabilities", biasModule.getOutPort("probabilities"));
		addModule(actionSelection);
		
		//Add extra input to bias Module
		biasModule.addInPort("action", actionSelection.getOutPort("action"),true);
		
		
		
		//DO REINFORCEMENT LEARNING
		//Create deltaSignal module
		Module deltaError = new ActorCriticDeltaError("error", discountFactor, numActions);
		deltaError.addInPort("reward", rewardModule.getOutPort("reward"));
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
		PlaceCellTransitionMatrixUpdater wUpdater = new PlaceCellTransitionMatrixUpdater("wUpdater", numPCPerSide*numPCPerSide, wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("wPort", WTable);
		addModule(wUpdater);
		
		
		
		//add possible movement info:
		float angle=0;
		double deltaAngle = 2*Math.PI/numActions;
		angles = new float[numActions];
		for (int i=0;i<numActions;i++)
		{	
			//actions[i] =new Point3f((float)Math.cos(angle)*stepSize,(float)Math.sin(angle)*stepSize,0);
			angles[i] = angle;
			angle+=deltaAngle;
		}
		
		
		//ADD DRAWING UTILITIES:
		VirtUniverse universe = VirtUniverse.getInstance();
		universe.addDrawingFunction(new DrawPolarGraph("Q softmax",50, 50, 50, softmax.probabilities,true));
		
		universe.addDrawingFunction(new DrawPolarGraph("gated probs",50, 170, 50, actionGating.probabilities,true));
		universe.addDrawingFunction(new DrawPolarGraph("biased probs",50, 290, 50, biasModule.probabilities,true));
		
		universe.addDrawingFunction(new DrawPolarGraph("bias ring",50, 410, 50, biasModule.chosenRing,true));
		
		universe.addDrawingFunction(new DrawCycleInformation(375, 50, 15));		
		
		
		
		

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
	
	
	Boolean done = false;
	int imid = 0;
	
	
	
	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
	}
	
	@Override
	public void finalTask() {
		// TODO Auto-generated method stub
		super.finalTask();
		
		Globals g = Globals.getInstance();
//		System.out.println("starting final task: "+g.get("cycle"));
		
		//update screen before moving:
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.render(true);
		if(subject.recorder!=null) subject.recorder.record();
		
//		System.out.println("back in final task after rendering: "+g.get("cycle"));
		
		//clear has eaten and tried to eat
		subject.setHasEaten(false);
		subject.clearTriedToEAt();
		
		
		//perform selected action
		LocalizableRobot robot = (LocalizableRobot)subject.getRobot();
		
		int action = ((Int0dPort)actionSelection.getOutPort("action")).get();
		float deltaAngle = GeomUtils.relativeAngle(angles[action], robot.getOrientationAngle());
		
		robot.executeAffordance(new TurnAffordance(deltaAngle, subject.step), subject);
		robot.executeAffordance(new ForwardAffordance(subject.step), subject);
		
		EatAffordance eat = new EatAffordance();
		
		if(robot.checkAffordance(eat)) robot.executeAffordance(eat, subject);
		
		
//		System.out.println("finished final task: "+g.get("cycle"));
		
//		try {
//			 new BufferedReader(new InputStreamReader(System.in)).readLine();;
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		
	}



}
