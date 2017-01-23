package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotAction;
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromPathModule;
import edu.usf.ratsim.nsl.modules.actionselection.FeederTraveler;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;
import edu.usf.ratsim.nsl.modules.input.HeadDirection;
import edu.usf.ratsim.nsl.modules.input.Position;
import edu.usf.ratsim.nsl.modules.input.SubjectAte;
import edu.usf.ratsim.nsl.modules.input.HighLevelCognition.CurrentFeederModule;
import edu.usf.ratsim.nsl.modules.input.Vision.VisibleFeedersModule;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class TSPModelFrance extends Model {

	public LinkedList<Boolean> ateHistory = new LinkedList<Boolean>();
	public LinkedList<float[]> pcActivationHistory = new LinkedList<float[]>();
	
	private int numActions;
	private TesselatedPlaceCellLayer placeCells;
	private FeederTraveler feederTraveler;
	
	private int numPCs;
	
	//INPUT MODULES
	HeadDirection hdModule;
	Position posModule;
	SubjectAte subAte;
	CurrentFeederModule currentFeeder;
	VisibleFeedersModule visibleFeeders;
	
	//CELL MODULES
	
	//ACTION SELECTION MODULES
	ActionFromPathModule actionFromPathModule;
	RandomFeederTaxicActionModule randomFeederTaxicActionModule;
	
	
	
	//REFERENCES for ease of access
	TSPSubjectFrance subject;
	VirtUniverse universe = VirtUniverse.getInstance();
	
	

	public TSPModelFrance() {
	}

	public TSPModelFrance(ElementWrapper params, TSPSubjectFrance subject,
			LocalizableRobot lRobot) {
		
//		 ////////////////////      MODULES DIAGRAM           //////////////////////////////////////////////// 
//		
//		
//		
//		
//		subAte----------
//			
//		currentFeeder-----------------
//		                             |
//									\/	
//		visibleFeedersModule -----> RandomTaxicFeederAction-------------
//																		|
//		                            ActionFromPath--------------------->*----------------FinalTask (choose action)
//		
//		Pos--->placeCells
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
		String sFeederOrder = params.getChildText("feederOrder");
		if(sFeederOrder.equals(".")){
			System.err.println("ERROR: feeder order defined as `.`, exiting program");
			System.exit(-1);
		}
		List<Integer> order = params.getChildIntList("feederOrder");
		
		
		String pathFile = params.getChild("pathFile").getText();

		numActions = subject.getPossibleAffordances().size();
		
		this.subject = subject;

		
		//CREATE MODULES OF THE MODEL
		
		//      INPUT MODULES
		
		hdModule = new HeadDirection("hd", (LocalizableRobot)subject.getRobot());
		addModule(hdModule);
		
		posModule = new Position("pos", (LocalizableRobot)subject.getRobot());
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
				"PCLayer", lRobot, PCRadius, numPCellsPerSide, placeCellType,
				xmin, ymin, xmax, ymax);
		numPCs = placeCells.getCells().size();
		addModule(placeCells);
		
		
		//       ACTION SELECTION MODULES
		
		//feeder taxic
		randomFeederTaxicActionModule = new RandomFeederTaxicActionModule("randomFeederTaxicActionModule");
		addModule(randomFeederTaxicActionModule);
		randomFeederTaxicActionModule.addInPort("currentFeeder", currentFeeder.getOutPort("currentFeeder"));
		randomFeederTaxicActionModule.addInPort("visibleFeeders", visibleFeeders.getOutPort("visibleFeeders"));
		
		
		//MOVE USING A PATH:
		actionFromPathModule = new ActionFromPathModule("actionFromPath", pathFile);
		//addModule(actionFromPathModule);		
		
		
		// Schme selection module:
		Module schemeSelector = new SchemeSelector("schemeSelector");
		addModule(schemeSelector);
		
		
		
		
		
	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public void newEpisode() {
		// TODO Auto-generated method stub
		
	}
	
	public void endEpisode(){
		//number of pace cells : numPCs;
		//ateHistory.add(ate);
		// pcActivationHistory.add(pcVals);
		
		
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
		
		System.out.println("subject ate? "+ ate);
		
		
		
		
		
		
		//System.out.println("Final Task");
		
		
		//perform action chosen with action from path module
		//MoveToAction action = (MoveToAction)actionFromPathModule.outport.data;
		//System.out.println(action);
		//VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(action.x(), action.y()), action.w());
		
		
		//PERFORM ACTION OF TAXIC MODULE
		//randomFeederTaxicActionModule.outport.data
		int destinyId = randomFeederTaxicActionModule.action.id();
		if(destinyId==-1){
			System.out.println("ERROR: CANT FIND A NEW FEEDER TO GO TO");
			System.exit(0);
			
		}else{
			
			Point3f feederPos = universe.getFeeder(destinyId).getPosition();		
			
			float hd = hdModule.hd.get();
			Point3f pos = posModule.pos.get();
			
			float angleToFeeder = (float)Math.atan2(feederPos.y - pos.y, feederPos.x - pos.x);
			angleToFeeder = GeomUtils.relativeAngle(angleToFeeder, hd);
			
			
			if(Math.abs(angleToFeeder) > subject.leftAngle){
				//must spin towards goal
				if(angleToFeeder < 0)
					subject.getRobot().executeAffordance(new TurnAffordance(subject.rightAngle, subject.step), subject);
				else
					subject.getRobot().executeAffordance(new TurnAffordance(subject.leftAngle, subject.step), subject);
				
			}else{
				//must move towards goal
				subject.getRobot().executeAffordance(new ForwardAffordance(subject.step), subject);
				//check if reached goal, and eat
				if( ((VirtualRobot)subject.getRobot()).withinEatingDistanceFromFeeder(destinyId)  ){
					subject.getRobot().executeAffordance(new EatAffordance(), subject);
					System.out.println("Try to eat from: "+destinyId);
					//disable feeder:
					universe.setEnableFeeder(destinyId, false);
				}
				
				
				
			}
			
		}
			
		
		
		
	}

}
