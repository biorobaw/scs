package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.io.IOException;

//import TRN4JAVA.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromPathModule;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.HighLevelCognition.CurrentFeederModule;
import edu.usf.ratsim.nsl.modules.input.Vision.VisibleFeedersModule;
import platform.simulatorVirtual.robots.PuckRobot;

public class TSPModelFranceLocal extends Model {

	public LinkedList<Boolean> ateHistory = new LinkedList<Boolean>();
	public LinkedList<float[]> pcActivationHistory = new LinkedList<float[]>();
	
	private TesselatedPlaceCellLayer placeCells;
	
	private int numPCs;
	
	//INPUT MODULES
	HeadDirection hdModule;
	Position posModule;
	SubjectAte subAte;
	CurrentFeederModule currentFeeder;
	VisibleFeedersModule visibleFeeders;
//	Reservoir reservoir = null;
	
	//CELL MODULES
	
	//ACTION SELECTION MODULES
	ActionFromPathModule actionFromPathModule;
	NonVisitedFeederSetModule nonVisitedFeederSetMoudle;
	RandomOrClosestFeederTaxicActionModule randomOrClosestFeederTaxicActionModule;
//	ReservoirActionSelectionModule reservoirActionSelectionModule = null;
	
	
	
	//REFERENCES for ease of access
	TSPSubjectFranceLocal subject;
	VirtUniverse universe = VirtUniverse.getInstance();
	
	

	public TSPModelFranceLocal() {
	}

	public TSPModelFranceLocal(ElementWrapper params, TSPSubjectFranceLocal subject,PuckRobot robot) {
		
//		 ////////////////////      MODULES DIAGRAM           //////////////////////////////////////////////// 
//		
//		
//		
//		
//		subAte----------
//			
//		currentFeeder------------------------------------------------
//		                             |								| 
//									\/								\/
//		visibleFeedersModule -->NonVisitedFeederSetModule---> RandomOrClosestTaxicFeederAction			
//																		|					
//		                            ActionFromPath--------------------->*--------------->FinalTask (choose action)--Action execution
//																		/\
//		Pos--->placeCells---------> ReservoirActionSelectionModule------|
//											/\	
//									   prediction
//		hd--------
//		
//		
//		Notes:
//		-The connection from pos to placeCells is implicit since placeCells get the position from the robot
//		
//		
//		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Get some configuration values for place cells + qlearning
		float PCRadius = params.getChildFloat("PCRadius");
		int numPCellsPerSide = params.getChildInt("numPCCellsPerSide");
		String placeCellType = params.getChildText("placeCells");
		float xmin = params.getChildFloat("xmin");
		float ymin = params.getChildFloat("ymin");
		float xmax = params.getChildFloat("xmax");
		float ymax = params.getChildFloat("ymax");
		
		List<Integer> order = params.getChildIntList("feederOrder");
		
		
		String pathFile = params.getChild("pathFile").getText();
		
		
		//BASIC NAVIGATION PARAMS:
		float filterVisitedFeedersProbability = params.getChildFloat("filterVisitedFeedersProbability");
		float moveToClosestFeederInSubsetProbability = params.getChildFloat("moveToClosestFeederInSubsetProbability");
		
		
		String sFeederOrder = params.getChildText("feederOrder");
		if(sFeederOrder.equals(".")){
			System.err.println("ERROR: feeder order defined as `.`, exiting program");
			System.exit(-1);
		}
		

		
		this.subject = subject;

		
		//CREATE MODULES OF THE MODEL
		
		//      INPUT MODULES
		
		hdModule = new HeadDirection("hd", subject.getRobot());
		addModule(hdModule);
		
		posModule = new Position("pos", subject.getRobot());
		addModule(posModule);
		
		subAte = new SubjectAte("subAte", subject);
		addModule(subAte);
		
		currentFeeder = new CurrentFeederModule("currentFeeder", subject);
		addModule(currentFeeder);
		
		visibleFeeders = new VisibleFeedersModule("visibleFeeders", subject );
		addModule(visibleFeeders);
		
		
		
		
		//       CELL MODULES 
		
		// palce cells
		placeCells = new TesselatedPlaceCellLayer(
				"PCLayer", PCRadius, numPCellsPerSide, placeCellType,
				xmin, ymin, xmax, ymax);
		numPCs = placeCells.getCells().size();
		placeCells.addInPort("position", posModule.getOutPort("position"));
		addModule(placeCells);
		
		
		//       ACTION SELECTION MODULES
		
		//TAXIC RELATED
		
		//feeder subselection
		nonVisitedFeederSetMoudle = new NonVisitedFeederSetModule("nonVisitedFeederSetModule", filterVisitedFeedersProbability);
		nonVisitedFeederSetMoudle.addInPort( "currentFeeder", currentFeeder.getOutPort("currentFeeder"));
		nonVisitedFeederSetMoudle.addInPort("feederSet", visibleFeeders.getOutPort("visibleFeeders"));
		addModule(nonVisitedFeederSetMoudle);
		
		//feeder taxic
		randomOrClosestFeederTaxicActionModule = new RandomOrClosestFeederTaxicActionModule("randomFeederTaxicActionModule",subject,moveToClosestFeederInSubsetProbability);
		addModule(randomOrClosestFeederTaxicActionModule);
		randomOrClosestFeederTaxicActionModule.addInPort("currentFeeder", currentFeeder.getOutPort("currentFeeder"));
		randomOrClosestFeederTaxicActionModule.addInPort("feederSet", nonVisitedFeederSetMoudle.getOutPort("feederSubSet"));
		
		
		//MOVE USING A PATH:
		actionFromPathModule = new ActionFromPathModule("actionFromPath", pathFile);
		//addModule(actionFromPathModule);	
		
		//Reservoir Action:
		/*  COMMENT OUT WHILE RESERVOIR IS NOT READY
		//                                   id, stim_size,reservoir_size, leak_rate, initial_State_scale, lr, epochs
		reservoir = new Reservoir( 0,  numPCs,	numPCs, 	1f, 	1f,	 0.5f,	 100);
		
		
		reservoirActionSelectionModule = new ReservoirActionSelectionModule("reservoirAction", reservoir);
		reservoirActionSelectionModule.addInPort("placeCells", placeCells.getOutPort("activation"));
		addModule(reservoirActionSelectionModule);
		*/
		
		// Schme selection module:
//		Module schemeSelector = new SchemeSelector("schemeSelector");
//		addModule(schemeSelector);
		
		
		//TRN4Java INITIALIZATION
//		TRN4JAVA.initialize_local(0, 0);
//		try {
//			TRN4JAVA.allocate(3);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		
		
		
		
		
	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	@Override
	public void newEpisode() {
		super.newEpisode();
		
//		System.out.println("press enter");
//		try {
//			do{
//				System.in.read();
//			} while(System.in.available()>0) ;
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// TODO Auto-generated method stub		
		//send reset signal to all modules that use memory:
		/* COMMENT OUT RESERVOIR
		reservoirActionSelectionModule.newEpisode();
		*/

		
	}
	

	
	public void endEpisode(){
		/*
		reservoir.train(pcActivationHistory, ateHistory);
		*/
//		reservoir.newEpisode();
		
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
					.getOutPort("activation")).getNonZero());
		return activation;
	}
	
	public void initialTask(){
		//System.out.println("Initial Task");
		
		// here, or in a new module, i should check weather a new calculation of a taxic action should be forced.
		
	}
	
	public void finalTask(){
		
		//append history:
		//number of pace cells : numPCs;
		Boolean ate = ((Bool0dPort)subAte.getOutPort("subAte")).get();
		float[] pcVals = ((Float1dSparsePortMap)placeCells.getOutPort("activation")).getData();
		
		ateHistory.add(ate);
		pcActivationHistory.add(pcVals);
		
		if(ate) System.out.println("subject ate? "+ ate);
		
		
		
		
		
		
		//System.out.println("Final Task");
		
		
		//perform action chosen with action from path module
		//MoveToAction action = (MoveToAction)actionFromPathModule.outport.data;
		//System.out.println(action);
		//VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(action.x(), action.y()), action.w());
		
		
		//PERFORM ACTION OF TAXIC MODULE
		//randomFeederTaxicActionModule.outport.data
		
		subject.robot.pendingActions.add(randomOrClosestFeederTaxicActionModule.action);
		
		
			
		
		
		
	}

}
