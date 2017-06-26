package edu.usf.ratsim.experiment.subject.TSPFrance;


import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.NonVisitedFeederSetModule;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.RandomOrClosestFeederTaxicActionModule;
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

public class TSPModelFrance extends Model {

	public LinkedList<Boolean> ateHistory = new LinkedList<Boolean>();
	public LinkedList<float[]> pcActivationHistory = new LinkedList<float[]>();
	public LinkedList<float[]> posHistory = new LinkedList<float[]>();
	private TesselatedPlaceCellLayer placeCells;	
	private int numPCs;
	
	//INPUT MODULES
	HeadDirection hdModule;
	Position posModule;
	SubjectAte subAte;
	CurrentFeederModule currentFeeder;
	VisibleFeedersModule visibleFeeders;
	Reservoir reservoir = null;
	static final int ID = 0;
	
	//CELL MODULES
	
	//ACTION SELECTION MODULES
	ActionFromPathModule actionFromPathModule;
	NonVisitedFeederSetModule nonVisitedFeederSetMoudle;
	RandomOrClosestFeederTaxicActionModule randomOrClosestFeederTaxicActionModule;
	ReservoirActionSelectionModule reservoirActionSelectionModule = null;
	
	
	
	Bool0dPort chooseNewFeeder = new Bool0dPort(initialModule);

	Bool0dPort finishReservoirAction = new Bool0dPort(initialModule);
	
	//REFERENCES for ease of access
	TSPSubjectFrance subject;
	VirtUniverse universe = VirtUniverse.getInstance();
	
	
	PuckRobot robot;
	
	

	public TSPModelFrance() {
	}

	public TSPModelFrance(ElementWrapper params, TSPSubjectFrance subject,PuckRobot robot) {
		
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
		
		this.robot = robot;
		
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
//		randomOrClosestFeederTaxicActionModule.addInPort("currentFeeder", currentFeeder.getOutPort("currentFeeder"));
		randomOrClosestFeederTaxicActionModule.addInPort("feederSet", nonVisitedFeederSetMoudle.getOutPort("feederSubSet"));
		randomOrClosestFeederTaxicActionModule.addInPort("newSelection", chooseNewFeeder);
		
		//MOVE USING A PATH:
		actionFromPathModule = new ActionFromPathModule("actionFromPath", pathFile);
		//addModule(actionFromPathModule);	
		
		//Reservoir Action:
		float initial_state_scale = params.getChildFloat("initialStateScale");
		int reservoir_size = params.getChildInt("reservoirSize");
		float leak_rate = params.getChildFloat("leakRate");
		
		float learning_rate = params.getChildFloat("learningRate");
		int snippets_size = params.getChildInt("snippetsSize");
		int snippets_per_burst = params.getChildInt("snippetsPerBurst");
		int burst_per_trial = params.getChildInt("burstPerTrial");
		int rows = params.getChildInt("rows");
		int cols = params.getChildInt("cols");
		float sigma = params.getChildFloat("sigma");
		int preamble = params.getChildInt("preamble");
		int stimulus_size = numPCellsPerSide * numPCellsPerSide;
		
		float response[] = new float[stimulus_size * rows * cols];
		
		for (int row = 0; row < rows; row++)
		{
			float y = (row / (float)(rows-1))*(ymax - ymin) + ymin;
			for (int col = 0; col < cols; col++)
			{
				float x = (col/ (float)(cols-1))*(xmax - xmin) + xmin;
					
				float activation[] = placeCells.getActivationValues(new Point3f(x, y, 0));
				for (int pc = 0; pc < stimulus_size; pc++)
				{
					response[pc * rows * cols + row * cols + col] = activation[pc];
				}
					//
			}
		
		}
		
		reservoir = new Reservoir(ID,
				stimulus_size,reservoir_size, leak_rate, initial_state_scale, learning_rate,
				snippets_size, snippets_per_burst, burst_per_trial,
				rows, cols, xmin, xmax, ymin, ymax, response, sigma, PCRadius,
				preamble);
		
		reservoirActionSelectionModule = new ReservoirActionSelectionModule("reservoirAction", reservoir);
		reservoirActionSelectionModule.addInPort("placeCells", placeCells.getOutPort("activation"));
		reservoirActionSelectionModule.addInPort("position", posModule.getOutPort("position"));
		reservoirActionSelectionModule.addInPort("finishedAction", finishReservoirAction);
		addModule(reservoirActionSelectionModule);
		
		// Schme selection module:
		Module schemeSelector = new SchemeSelector("schemeSelector");
		addModule(schemeSelector);
		
		
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
		reservoir.newEpisode();
		super.newEpisode();
		// TODO Auto-generated method stub		
		//send reset signal to all modules that use memory:
		// COMMENT OUT RESERVOIR
		//reservoir.newEpisode();
		//reservoirActionSelectionModule.newEpisode();
	
		

		
	}
	

	
	public void endEpisode(){
		
		if (!pcActivationHistory.isEmpty() && !posHistory.isEmpty() && !ateHistory.isEmpty())
		{
			reservoir.train(pcActivationHistory, posHistory, ateHistory);
			pcActivationHistory.clear();
			posHistory.clear();
			ateHistory.clear();
		}
		
//		reservoir.newEpisode();
		
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
		
		finishReservoirAction.set(robot.actionMessageBoard.get(MoveToAction.actionID) != null || robot.actionMessageBoard.get(FeederTaxicAction.actionID) != null);
	
		
		// here, or in a new module, i should check weather a new calculation of a taxic action should be forced.
		
		
		
	}
	
	public void finalTask(){
		
		//append history:
		//number of pace cells : numPCs;
	
		Boolean ate = ((Bool0dPort)subAte.getOutPort("subAte")).get();
		if(ate) System.out.println("subject ate? "+ ate);
		
		Boolean finishedAction = finishReservoirAction.get();
		
		/*if (finishedAction)
		{*/
			//((Float1dSparsePortMap)getInPort("placeCells")).getData()
			Point3f pos = ((Point3fPort)posModule.getOutPort("position")).get();
			float activation_pattern[] = ((Float1dSparsePortMap)placeCells.getOutPort("activation")).getData();
			float estimated_position[] = {pos.x, pos.y};
		
			ateHistory.add(ate);
			pcActivationHistory.add(activation_pattern);
			posHistory.add(estimated_position);
		/*}*/
		
		//System.out.println("Final Task");
		
		
		//perform action chosen with action from path module
		//MoveToAction action = (MoveToAction)actionFromPathModule.outport.data;
		//System.out.println(action);
		//VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(action.x(), action.y()), action.w());
		
		
		//PERFORM ACTION OF TAXIC MODULE
		//randomFeederTaxicActionModule.outport.data
		

		if ((Integer)Globals.getInstance().get("episode") > 0)
		{
			System.out.println("RESERVOIR ACTION SELECTED");
	
			subject.robot.pendingActions.add(reservoirActionSelectionModule.action);
		}
		else
		{
			System.out.println("RANDOM FEEDER ACTION SELECTED");
			subject.robot.pendingActions.add(randomOrClosestFeederTaxicActionModule.action);
		}
				
	}

}
