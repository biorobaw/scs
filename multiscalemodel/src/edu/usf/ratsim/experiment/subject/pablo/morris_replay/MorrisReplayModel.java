package edu.usf.ratsim.experiment.subject.pablo.morris_replay;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

//import TRN4JAVA.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.NonVisitedFeederSetModule;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.RandomOrClosestFeederTaxicActionModule;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawCycleInformation;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPath;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.HighLevelCognition.CurrentFeederModule;
import edu.usf.ratsim.nsl.modules.input.Vision.VisibleFeedersModule;
import edu.usf.ratsim.nsl.modules.multipleT.PlaceCellTransitionMatrixUpdater;
import platform.simulatorVirtual.robots.PuckRobot;

public class MorrisReplayModel extends Model {

	
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
	NonVisitedFeederSetModule nonVisitedFeederSetMoudle;
	RandomOrClosestFeederTaxicActionModule randomOrClosestFeederTaxicActionModule;
	
	
	Bool0dPort chooseNewFeeder = new Bool0dPort(initialModule);
	
	
	//REFERENCES for ease of access
	MorrisReplaySubject subject;
	VirtUniverse universe = VirtUniverse.getInstance();
	
	
	//Replay Model
	SimpleReplayModel replay = null;
	int numReplays;
	float replayThreshold;
	DrawPath drawPath = null;
	DrawPath drawRealPath = null;
	
	
	PuckRobot robot;
	
	//path matrix for replay:
	private Float2dSparsePort WTable;
	
	

	public MorrisReplayModel() {
	}

	public MorrisReplayModel(ElementWrapper params, MorrisReplaySubject subject,PuckRobot robot) {
		
//		 ////////////////////      MODULES DIAGRAM           //////////////////////////////////////////////// 
//		
//		
//		
//		
//			
//		currentFeeder------------------------------------------------
//		                             |								| 
//									\/								\/
//		visibleFeedersModule -->NonVisitedFeederSetModule---> RandomOrClosestTaxicFeederAction			
//																		|					
//		                                                                *--------------->FinalTask (choose action)--Action execution
//																		
//		Pos--->placeCells------->updateW
//												
//									   
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
		
		numReplays = params.getChildInt("numReplays");
		replayThreshold = params.getChildFloat("replayThreshold");
		
		this.robot = robot;		
		
		
		
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
		
		
		//UPDATE PATH MATRIX
		int numPC = placeCells.getCells().size();
		float wTransitionLR = 1.0f; //matrix learning rate
		WTable = new Float2dSparsePortMatrix(null, numPC, numPC);
		PlaceCellTransitionMatrixUpdater wUpdater = new PlaceCellTransitionMatrixUpdater("wUpdater", numPC, wTransitionLR);
		wUpdater.addInPort("PC", placeCells.getOutPort("activation"));
		wUpdater.addInPort("wPort", WTable);
		addModule(wUpdater);
		
		//create replay model
		replay = new SimpleReplayModel(numPC, WTable);
		
		
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
		//randomOrClosestFeederTaxicActionModule.addInPort("currentFeeder", currentFeeder.getOutPort("currentFeeder"));
		randomOrClosestFeederTaxicActionModule.addInPort("feederSet", nonVisitedFeederSetMoudle.getOutPort("feederSubSet"));
		randomOrClosestFeederTaxicActionModule.addInPort("newSelection", chooseNewFeeder);
		
		
		
		
		universe.addDrawingFunction(new DrawCycleInformation(375, 50, 15));	
		drawPath = new DrawPath(-1, 1, -1, 1);
		universe.addDrawingFunction(drawPath);
		
		drawRealPath = new DrawPath(-1,1,-1,1);
		drawRealPath.setColor(Color.blue);
		universe.addDrawingFunction(drawRealPath);
		
		
		
		
	}

	public void newTrial() {
		
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	@Override
	public void newEpisode() {
		super.newEpisode();
		drawRealPath.path.clear();
		
		
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

		
	}
	

	
	public void endEpisode(){
		
		//open log file
		String logFileName =Globals.getInstance().get("logPath") + "/replays.bin" ;
		OutputStream of = BinaryFile.openFileToWrite(logFileName);
		
		//get virtual universe for displaying
		VirtUniverse vu = VirtUniverse.getInstance();
		boolean displayReplays = true;
		
		
		//generate replays,log them, display them
		for (int i=0;i<numReplays; i++) {
			
			LinkedList<Integer> r = replay.doSimpleReplay(500, replayThreshold);
			System.out.println("replay " + i + "   " +r.size());
			BinaryFile.writeIntList(of, r);
			
			
			
			if(displayReplays){
				LinkedList<Point3f> rpath = new LinkedList<Point3f>();
				drawPath.setPath(rpath);
				for(int id : r){
					rpath.add(placeCells.getCells().get(id).getPreferredLocation());
					vu.render(true);
				}
			}
			
		}
		
		
		drawPath.path.clear();
		//close log
		try {
			of.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
					.getOutPort("activation")).getNonZero());
		return activation;
	}
	
	@Override
	public void initialTask(){
		//System.out.println("Initial Task");
		chooseNewFeeder.set(robot.actionMessageBoard.get(FeederTaxicAction.actionID) != null);
		
	}
	
	
	
	
	public void finalTask(){
				
		drawRealPath.path.add(posModule.pos.get());
		subject.robot.pendingActions.add(randomOrClosestFeederTaxicActionModule.action);
		

	}

}
