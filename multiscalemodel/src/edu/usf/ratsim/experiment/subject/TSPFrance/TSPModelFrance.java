package edu.usf.ratsim.experiment.subject.TSPFrance;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.experiment.robot.specificActions.TeleportToAction;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.experiment.subject.TSPFrance.prerecorded.PrerecordedPathModule;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.NonVisitedFeederSetModule;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.RandomOrClosestFeederTaxicActionModule;
import edu.usf.ratsim.experiment.subject.pablo.mymodules.TaxicNextFeederFromFileModule;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawCycleInformation;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromPathModule;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCellNoThresholding;
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
	long simulation_id;
	static final int DEFAULT_INDEX = 0; // 0 = CPU, > 0 = GPU
	//BASIC MODEL RUNS VS RESERVOIR RUNS:
	int basicRuns = 1;
	int reservoirRuns = 1;
	//String basicRunMode = "pq"; //pq or fromFile, when mode=fromFile, next feeder is chosen according to a file

	
	//CELL MODULES
	
	//ACTION SELECTION MODULES
	//ActionFromPathModule actionFromPathModule;
	NonVisitedFeederSetModule nonVisitedFeederSetMoudle;
	RandomOrClosestFeederTaxicActionModule randomOrClosestFeederTaxicActionModule;
	ReservoirActionSelectionModule reservoirActionSelectionModule = null;
	private TaxicNextFeederFromFileModule taxicNextFeederFromFileModule =null;
	
	
	 String targetSequenceFileName;
	 
	Bool0dPort chooseNewFeeder = new Bool0dPort(initialModule);

	Bool0dPort finishReservoirAction = new Bool0dPort(initialModule);
	
	//REFERENCES for ease of access
	TSPSubjectFrance subject;
	VirtUniverse universe = VirtUniverse.getInstance();
	
	//private int episode;

	PuckRobot robot;
	
	Integer runLevel = 2; //0 = only scs, 1 = only reservoir, 2 = first scs and then reservoir
	private PrerecordedPathModule prerecordedPath = null;
	private int basicActionMechanism;

	public static void debug_2D_buffer(float buffer[], int rows, int cols)
	{
		   final BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = (Graphics2D)img.getGraphics();
	  
	        for(int j = 0; j < rows; j++) {
	        	for(int i = 0; i < cols; i++) {
	                float c = (float) buffer[j * cols + i];
	                g.setColor(new Color(c, c, c));
	                g.fillRect(i, j, 1, 1);
	            }
	        }

	        
	        JFrame frame = new JFrame("Image test");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        JPanel panel = new JPanel() 
	        {
	            @Override
	            protected void paintComponent(Graphics g) 
	            {
	                Graphics2D g2d = (Graphics2D)g;
	                g2d.clearRect(0, 0, getWidth(), getHeight());
	                g2d.setRenderingHint(
	                        RenderingHints.KEY_INTERPOLATION,
	                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	                        // Or _BICUBIC
	                g2d.scale(2, 2);
	                g2d.drawImage(img, 0, 0, this);
	            }
	        };
	        panel.setPreferredSize(new Dimension(1024, 1024));
	        frame.getContentPane().add(panel);
	        frame.pack();
	        frame.setVisible(true);
	}
	
	
	
	private void test_response(float x, float y, float response[], int PC, int ROWS, int COLS)
	{
		float xmin = -1;
		float ymin = -1;
		float xmax= 1;
		float ymax = 1;
		float activation[] = placeCells.getActivationValues(new Point3f(x, y, 0.0f));
		float hypothesis[] = new float[PC * ROWS * COLS];
		float scale[] = new float[PC]; 
		for (int place_cell = 0; place_cell < PC; place_cell++)
		{
			float sum = 0.0f;
			for (int row = 0; row < ROWS; row++)
			{
				for (int col = 0; col < COLS; col++)
				{
					float dv = activation[place_cell] - response[place_cell * ROWS * COLS + row * COLS + col];
					float h = (float)Math.exp(-dv*dv);
					hypothesis[place_cell * ROWS * COLS + row * COLS + col] = h;
					sum +=h;
					
				}
			}
			scale[place_cell] = 1.0f / sum;
		}
		float location[] = new float[ROWS * COLS]; 
		float M = 0.0f;
		float m = 1.0f;
		int argmax_row = -1;
		int argmax_col = -1;
		for (int row = 0; row < ROWS; row++)
		{
			for (int col = 0; col < COLS; col++)
			{
				float sum = 0.0f;
				for (int place_cell = 0; place_cell < PC; place_cell++)
				{
					sum += hypothesis[place_cell * ROWS * COLS + row * COLS + col] * scale[place_cell];
				}
				location[row * COLS + col] = sum;
				if (sum < m)
					m = sum;
				if (sum > M)
				{
					M = sum;
					argmax_row = row;
					argmax_col = col;
				}
			}
		}
		
		float argmax_y = (argmax_row / (float)(ROWS - 1)) * (ymax - ymin) + ymin; 
		float argmax_x = (argmax_col / (float)(COLS - 1)) * (xmax - xmin) + xmin; 	
		for (int row = 0; row < ROWS; row++)
		{
			for (int col = 0; col < COLS; col++)
			{
				location[row * COLS + col] = (location[row * COLS + col] - m) / (M - m);
			}
		}
		
		float dx = argmax_x - x;
		float dy = argmax_y - y; 
		float error = (float)Math.sqrt(dx*dx + dy*dy);
		
		//debug_2D_buffer(location, ROWS, COLS);
		System.out.println("Error " + error);
	}
	
	public TSPModelFrance()
	{
	}

	public TSPModelFrance(ElementWrapper params, TSPSubjectFrance subject,PuckRobot robot) 
	{
		
		String component = (String)Globals.getInstance().get("component");
		if(component==null ) runLevel = 2;
		else if (component.equals("scs")) runLevel = 0;
		else if (component.equals("reservoir")) runLevel = 1;
		else {
			System.out.println("ERROR - console parameter comonent should either be scs , reservoir or not specified");
			System.exit(-1);
		}
		
		
		
		int experimentID = params.getChildInt("experimentID"); //0= no snippets experiment, 1 = single trajectory + snippets, 2 = ABCDE whole experiment;
		//select input method:
		basicActionMechanism = 1; // 0 = taxicNextFeederModule  ;   1 = prerecorded Path   ; 2 = PQ
		
		switch(basicActionMechanism) {
		case 0:
			//Taxic feeder module - used when moving in sequence of feeders
			taxicNextFeederFromFileModule = new TaxicNextFeederFromFileModule("taxicFromFile", params.getChildText("taxicPaths"));
			taxicNextFeederFromFileModule.addInPort("newSelection", chooseNewFeeder);
			addModule(taxicNextFeederFromFileModule);
			break;
			
		case 1: 
			//Prerecorded path module - used to follow a prerecorded path, rewards are taken from the path file
			String abcedFile = params.getChildText("abcedFile");
			String bacdeFile = params.getChildText("bacdeFile");
			String ebcdaFile = params.getChildText("ebcdaFile");
			String abcdeFile = params.getChildText("abcdeFile");
			
			targetSequenceFileName = abcdeFile;
			
			//String fileSets[] = new String[] {abcdeFile,abcdeFile,bacdeFile + "," + ebcdaFile+","+ abcdeFile};
			String fileSets[] = new String[] {abcdeFile,abcdeFile,bacdeFile + "," + ebcdaFile+","+ abcedFile};
			String fileSet =fileSets[experimentID];

			
			prerecordedPath = new PrerecordedPathModule("recorded path", fileSet);
			addModule(prerecordedPath);
			break;
			
		case 2: 
			//PQ strategy:
			float filterVisitedFeedersProbability = params.getChildFloat("filterVisitedFeedersProbability");
			float moveToClosestFeederInSubsetProbability = params.getChildFloat("moveToClosestFeederInSubsetProbability");
			
			currentFeeder = new CurrentFeederModule("currentFeeder", subject);
			addModule(currentFeeder);
			
			visibleFeeders = new VisibleFeedersModule("visibleFeeders", subject );
			addModule(visibleFeeders);
			
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
			randomOrClosestFeederTaxicActionModule.addInPort("newSelection", chooseNewFeeder);
			
		}

		

	
		
		
		
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
		

		float learn_reverse_rate = params.getChildFloat("learnReverseRate");
		float generate_reverse_rate = params.getChildFloat("generateReverseRate");
		float reverse_learning_rate = params.getChildFloat("reverseLearningRate");
		float discount = params.getChildFloat("discount");
		int mini_batch_size = params.getChildInt("miniBatchSize");
		float angle = params.getChildFloat("angle");
		
		reservoirRuns = params.getChildInt("reservoirRuns");
		basicRuns	  = params.getChildInt("basicRuns");
		//basicRunMode  = params.getChildText("basicRunMode");
		
		
		
		this.robot = robot;		
		this.subject = subject;
		
		
		//String sFeederOrder = params.getChildText("feederOrder");
		//if(sFeederOrder.equals(".")){
		//	System.err.println("ERROR: feeder order defined as `.`, exiting program");
		//	System.exit(-1);
		//}
		
		
		//CREATE MODULES OF THE MODEL
		
		//      INPUT MODULES
		
		hdModule = new HeadDirection("hd", subject.getRobot());
		addModule(hdModule);
		
		posModule = new Position("pos", subject.getRobot());
		addModule(posModule);
		
		subAte = new SubjectAte("subAte", subject);
		addModule(subAte);
		
		
		
		
		
		
		
		
		//       CELL MODULES 
		
		// palce cells
		placeCells = new TesselatedPlaceCellLayer(
				"PCLayer", PCRadius, numPCellsPerSide, "ExponentialNoThresholding",
				xmin, ymin, xmax, ymax);

		List<PlaceCell> cells = placeCells.getCells();
		float width[] = new float[cells.size()];
		float cx[] = new float[cells.size()];
		float cy[] = new float[cells.size()];
		int k = 0;
		for (PlaceCell cell : cells) 
		{
			
		   width[k] = -1.0f/((ExponentialPlaceCellNoThresholding)cell).getWidth();
		   cx[k] = ((ExponentialPlaceCellNoThresholding)cell).getCenter().x;
		   cy[k] = ((ExponentialPlaceCellNoThresholding)cell).getCenter().y;
		  // System.out.println(cx[k] + "\t" + cy[k] + "\t" + width[k]);
		   k++;
		}
		//compare with place cells .csv
		numPCs = placeCells.getCells().size();
		placeCells.addInPort("position", posModule.getOutPort("position"));
		addModule(placeCells);
		
		
		//       ACTION SELECTION MODULES
		
		
		
		//MOVE USING A PATH:
		//actionFromPathModule = new ActionFromPathModule("actionFromPath", pathFile);
		//addModule(actionFromPathModule);	
		
		//Reservoir Action:

		float initial_state_scale = params.getChildFloat("initialStateScale");
		int reservoir_size = params.getChildInt("reservoirSize");
		float leak_rate = params.getChildFloat("leakRate");
		
		float learning_rate = params.getChildFloat("learningRate");
		int time_budget = params.getChildInt("timeBudget");
		int snippets_size = params.getChildInt("snippetsSize");
		int rows = params.getChildInt("rows");
		int cols = params.getChildInt("cols");
		float sigma = params.getChildFloat("sigma");
		float radius = params.getChildFloat("radius");
		float scale = params.getChildFloat("scale");
		int preamble = params.getChildInt("preamble");
		int stimulus_size = numPCellsPerSide * numPCellsPerSide;
		
		
		String severity = params.getChild("loggingSeverity").getText().toUpperCase();
		
		if(runLevel>0) {
			switch (severity)
			{
			case "TRACE" :
				TRN4JAVA.Basic.Logging.Severity.Trace.setup();
				break;
			case "DEBUG" :
				TRN4JAVA.Basic.Logging.Severity.Debug.setup();
				break;
			case "INFORMATION" :
				TRN4JAVA.Basic.Logging.Severity.Information.setup();
				break;
			case "WARNING" :
				TRN4JAVA.Basic.Logging.Severity.Warning.setup();
				break;
			case "ERROR" :
				TRN4JAVA.Basic.Logging.Severity.Error.setup();
				break;
			default :
				throw new IllegalArgumentException(severity);
			}
		
	
			TRN4JAVA.Advanced.Engine.Events.Trained.install(new TRN4JAVA.Advanced.Engine.Events.Trained()
			{
				@Override
				public void callback(final long simulation_id, final long evaluation_id)
				{
					//System.out.println("Simulation " + simulation_id + " is trained");
	
					reservoir.onTrained(simulation_id, evaluation_id);
				}
			});
			TRN4JAVA.Advanced.Engine.Events.Tested.install(new TRN4JAVA.Advanced.Engine.Events.Tested()
			{
				@Override
				public void callback(final long simulation_id, final long evaluation_id)
				{
					//System.out.println("Simulation " + simulation_id + " is tested");
					reservoir.onTested(simulation_id, evaluation_id);
				}
			});
			TRN4JAVA.Advanced.Engine.Events.Primed.install(new TRN4JAVA.Advanced.Engine.Events.Primed()
			{
				@Override
				public void callback(final long simulation_id, final long evaluation_id)
				{
					//System.out.println("Simulation " + simulation_id + " is primed");
					reservoir.onPrimed(simulation_id, evaluation_id);
				}
			});
			TRN4JAVA.Advanced.Engine.Events.Completed.install(new TRN4JAVA.Advanced.Engine.Events.Completed()
			{
				@Override
				public void callback()
				{
					//System.out.println("Simulations completed");
				}
			});
			TRN4JAVA.Advanced.Engine.Events.Allocated.install(new TRN4JAVA.Advanced.Engine.Events.Allocated()
			{
				@Override
				public void callback(final long id, final int rank)
				{
					//System.out.println("Simulation " + id + " allocated on processor rank " + rank);
				}
			});
			
		
			ElementWrapper wr = params.getChild("backendType");
			String backendType = "local";
			if (wr == null)
			{
				System.out.println("backendTypeWrapper parameter does not exist. Using default type " + backendType);
			}
			else
			{
				backendType = params.getChildText("backendType");
			}
			switch (backendType.toUpperCase())
			{
				case "LOCAL" :
				{
					int index = DEFAULT_INDEX;
					wr = params.getChild("backendDeviceIndex");
					if (wr == null)
					{
						System.out.println("deviceIndex parameter does not exist. Using default index " + index);
					}
					else
					{
						index = params.getChildInt("backendDeviceIndex");
					}
					TRN4JAVA.Basic.Engine.Backend.Local.initialize(new int []{index});
				}
				break;
				
				case "REMOTE" :
				{
					String host = "127.0.0.1";
					short port = 12345;
					wr = params.getChild("backendHost");
					if (wr == null)
					{
						System.out.println("backendHost parameter does not exist. Using default host " + host);
					}
					else
					{
						host = params.getChildText("backendHost");
					}
					wr = params.getChild("backendPort");
					if (wr == null)
					{
						System.out.println("backendPort parameter does not exist. Using default port " + port);
					}
					else
					{
						port = (short)params.getChildInt("backendPort");
					}
					TRN4JAVA.Basic.Engine.Backend.Remote.initialize(host, port);
				}
				break;
				
				default :
				      throw new IllegalArgumentException("Invalid backend type :" + backendType);
			}
		
			boolean callbacks_installed = false;
			for (ElementWrapper plugin : params.getChildren("plugin"))
			{
				String plugin_name = plugin.getChildText("name");
				String plugin_interface = plugin.getChildText("interface").toUpperCase();
				String plugin_path = plugin.getChildText("path");
				java.util.Map<String, String> plugin_arguments = new java.util.HashMap<String,String>();
				for (ElementWrapper argument : plugin.getChildren("argument"))
				{
					String argument_key = argument.getChildText("key");
					String argument_value = argument.getChildText("value");
					plugin_arguments.put(argument_key,  argument_value);
				}
				switch (plugin_interface)
				{
					case "CUSTOM" :
						TRN4JAVA.Custom.Plugin.initialize(plugin_path, plugin_name, plugin_arguments);
						break;
					case "CALLBACKS" :
						TRN4JAVA.Callbacks.Plugin.initialize(plugin_path, plugin_name, plugin_arguments);
						callbacks_installed = true;
						break;
					case "SEQUENCES" :
						TRN4JAVA.Sequences.Plugin.initialize(plugin_path, plugin_name, plugin_arguments);
						break;
					default :
					      throw new IllegalArgumentException("Invalid plugin interface :" + plugin_interface);
				}
			}
			//test_response( -0.3f, -1.0f, response, stimulus_size, rows, cols);
		//			TRN4JAVA.Basic.Simulation.Identifier identifier = new TRN4JAVA.Basic.Simulation.Identifier((short)1, (short)condition_number, simulation_number);
	
			TRN4JAVA.Basic.Simulation.Identifier identifier = new TRN4JAVA.Basic.Simulation.Identifier((short)1, (short)params.getChildInt("condition_number"), params.getChildInt("simulation_number"));
			simulation_id = TRN4JAVA.Basic.Simulation.encode(identifier);
		
			
			long 	replay_seed = RandomSingleton.getInstance().nextLong();
			long	consolidation_seed = RandomSingleton.getInstance().nextLong();
			long	decoder_seed = RandomSingleton.getInstance().nextLong();
			
			TRN4JAVA.Extended.Simulation.allocate(simulation_id);	
			reservoir = new Reservoir(
					callbacks_installed,
					simulation_id,
					replay_seed,
					consolidation_seed,
					decoder_seed,
					stimulus_size, reservoir_size, leak_rate, initial_state_scale, learning_rate, mini_batch_size,
					snippets_size, time_budget,learn_reverse_rate, generate_reverse_rate, reverse_learning_rate, discount,
					rows, cols, xmin, xmax, ymin, ymax, cx, cy, width, sigma, radius, scale, angle,
					preamble );
		
			reservoirActionSelectionModule = new ReservoirActionSelectionModule("reservoirAction", reservoir);
			reservoirActionSelectionModule.addInPort("placeCells", placeCells.getOutPort("activation"));
			reservoirActionSelectionModule.addInPort("position", posModule.getOutPort("position"));
			reservoirActionSelectionModule.addInPort("finishedAction", finishReservoirAction);
			addModule(reservoirActionSelectionModule);
		}
		
		// Schme selection module:
		//Module schemeSelector = new SchemeSelector("schemeSelector");
		//addModule(schemeSelector);
		
		
		universe.addDrawingFunction(new DrawCycleInformation(375, 50, 15));
		
		/*
		 * 	public static class Identifier
	{
		public short frontend_number;
		public short condition_number;
		public int simulation_number;
	}

	public static native long	encode(final Identifier identifier);
	public static native Identifier	decode(final long id);
		 * 
		 * */
		
		//TRN4Java INITIALIZATION
//		TRN4JAVA.initialize_local(0, 0);
//		try {
//			TRN4JAVA.allocate(3);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		
		
		
		
//		episode = 0;
	}


	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	@Override
	public void newEpisode() {
		//episode++;
		//if(reservoir.newEpisode();
		super.newEpisode();
		reservoir.newEpisode();
		if(basicActionMechanism==1) {
			TeleportToAction tta = prerecordedPath.firstPosition();
			robot.teleportTo(tta.x(), tta.y(), tta.theta());
		}
		// TODO Auto-generated method stub		
		//send reset signal to all modules that use memory:
		// COMMENT OUT RESERVOIR
		//reservoir.newEpisode();
		//reservoirActionSelectionModule.newEpisode();
		
	
		

		
	}
	

	
	public void endEpisode(){
		int episode = (Integer)Globals.getInstance().get("episode");
		String sequenceName = "episode_" + episode;
		
		//System.out.println("EPISODE: " +Globals.getInstance().get("episode"));
		
		
		if (!pcActivationHistory.isEmpty() && !posHistory.isEmpty() && !ateHistory.isEmpty())
		{
			if(runLevel>0)
				reservoir.gather(sequenceName, pcActivationHistory, posHistory, ateHistory);
			pcActivationHistory.clear();
			posHistory.clear();
			ateHistory.clear();
		}
		
		switch(runLevel) {
			case 0://only scs
				basicRunEndEpisode();
				
				//reservoir.train();
				//reservoir.endEpisode();
				break;
			case 1: // only reservoir
				//reservoir.train();
				reservoir.endEpisode();
				break;
			case 2: //both scs and reservoir
				if (!isReservoirEpisode()) {
					//advance path from file
					basicRunEndEpisode();
					
				} 
				if(isLastTrainingEpisode()) {
			
					reservoir.train();
					addTargetSequence();
				}
				reservoir.endEpisode();
		}

		
		

		
		
	}
	
	public void basicRunEndEpisode() {
		switch(basicActionMechanism) {
		case 0:
			//feeder taxic ordered
			if(!isLastTrainingEpisode())
				taxicNextFeederFromFileModule.nextPathInList();
			break;
		case 1:
			//prerecorded path
			if(!isLastTrainingEpisode())
				prerecordedPath.nextPathInList();
			break;
			
		case 2:
			//pq
			break;
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
		if(!isReservoirEpisode()) {
			if(runLevel==0 || runLevel==2) {
				switch(basicActionMechanism) {
				case 0 :
					//Taxic Feeder Module
					chooseNewFeeder.set(robot.actionMessageBoard.get(FeederTaxicAction.actionID) != null || (Integer)Globals.getInstance().get("cycle")==0);
					break;
				case 1 :
					//Prerecorded path
					subject.hasEaten = prerecordedPath.expectReward;
					break;
				case 2 :
					//PQ strategy
					chooseNewFeeder.set(robot.actionMessageBoard.get(FeederTaxicAction.actionID) != null || (Integer)Globals.getInstance().get("cycle")==0);
					break;
				}
				
				
			}
		}
		else if(runLevel>0) {
			finishReservoirAction.set(robot.actionMessageBoard.get(MoveToAction.actionID) != null || robot.actionMessageBoard.get(FeederTaxicAction.actionID) != null);
		}
		
		// here, or in a new module, i should check weather a new calculation of a taxic action should be forced.
		
		
		
	}
	
	public boolean isReservoirEpisode() {
		return runLevel==1 || ( runLevel==2 &&   (((Integer)Globals.getInstance().get("episode")) % (basicRuns+reservoirRuns) >= basicRuns));
	}
	
	public boolean isLastTrainingEpisode() {
		return (((Integer)Globals.getInstance().get("episode")) % (basicRuns+reservoirRuns)) == (basicRuns-1);
	}
	
	public void finalTask(){
		
		//append history:
		//number of pace cells : numPCs;
	
	
//		call_number++;
//		if (call_number > 3)
//		{
		Boolean finishedAction = runLevel > 0 && finishReservoirAction.get();
		Boolean isBasicRun =  !isReservoirEpisode();//runLevel==0 || runLevel==2 &&   (((Integer)Globals.getInstance().get("episode")) % (basicRuns+reservoirRuns+1) <= basicRuns);
		
		
		if (finishedAction || isBasicRun)
		{
			Boolean ate = ((Bool0dPort)subAte.getOutPort("subAte")).get();
			//if(ate) System.out.println("subject ate? "+ ate);
			float activation_pattern[] = ((Float1dSparsePortMap)placeCells.getOutPort("activation")).getData();
			//((Float1dSparsePortMap)getInPort("placeCells")).getData()
			Point3f pos = ((Point3fPort)posModule.getOutPort("position")).get();
			float estimated_position[] = {pos.x, pos.y};
		
			boolean append = true;
			float displacement = 0.0f;
			if (!posHistory.isEmpty())
			{
				float last_position[] = posHistory.getLast();
		
				float dx2 = (float)Math.pow(last_position[0] - estimated_position[0], 2);
				float dy2 = (float)Math.pow(last_position[1] - estimated_position[1], 2);
				displacement = (float)Math.sqrt(dx2 + dy2);
			
				append = displacement > 0.0f;
			}
			
			if (append && isBasicRun)
			{
				//System.out.println("displacement : " + displacement);
				ateHistory.add(ate);
				pcActivationHistory.add(activation_pattern);
				posHistory.add(estimated_position);
			}
			else
			{
				assert(ate == false);
				//System.out.println("No motion");
			}
		}
		
		
		
		
		
		
		
		
		
		
		/*}*/
		
		//System.out.println("Final Task");
		
		
		//perform action chosen with action from path module
		//MoveToAction action = (MoveToAction)actionFromPathModule.outport.data;
		//System.out.println(action);
		//VirtUniverse.getInstance().setRobotPosition(new Point2D.Float(action.x(), action.y()), action.w());
		
		
		//PERFORM ACTION OF TAXIC MODULE
		//randomFeederTaxicActionModule.outport.data
		/*
		 * episode 1 -> gather(sequnece1)
		 * episode 2 -> gather(sequence2)
		 * episode 3 -> gather(sequence3) + train()
		 * episode 4 -> gather(sequence4)
		 * episode 5 -> test(sequence4)
		 * episode 6 -> gather(sequnece1)
		 * episode 7 -> gather(sequence2)
		 * episode 8 -> gather(sequence3) + train()
		 * episode 9 -> gather(sequence4)
		 * episode 10 -> test(sequence4)
		 */
		
		
		//System.out.println(subject.robot.pendingActions.size() + " ");
		if (isBasicRun)
		{
			//System.out.println("Basic RUN");
			//if(basicRunMode.equals("pq")) {
			//	
			//	subject.robot.pendingActions.add(randomOrClosestFeederTaxicActionModule.action);
			//	
			//}
			//else 
			//if(basicRunMode.equals("fromFile")) {
				//System.out.println("FROM FILE: " + taxicNextFeederFromFileModule.action);
			
			
			
				
			
			//check whether episode ended:
			if(basicActionMechanism==0) {
				subject.robot.pendingActions.add(taxicNextFeederFromFileModule.action);
				Globals.getInstance().put("done",taxicNextFeederFromFileModule.completedPath() );
			}
			else if (basicActionMechanism==1) {
				TeleportToAction a = (TeleportToAction)prerecordedPath.outport.get();
				prerecordedPath.printPathPercentage();
				//System.out.println("To: "+a.x() + " " + a.y() + " " +a.theta());
				//System.out.println("Path eneded: "+prerecordedPath.pathEnded);
				subject.robot.pendingActions.add(a);
				Globals.getInstance().put("done", prerecordedPath.pathEnded);
			}
			
			//}
				
			//else {
			//	System.out.println("ERROR: basicRunMode value is not valid");
			//	System.exit(-1);
			//}
		//}else if((Integer)Globals.getInstance().get("episode") % (basicRuns+reservoirRuns+1) == basicRuns) {
			
			//System.out.println("TARGET SEQUENCE");
		//	subject.robot.pendingActions.add(taxicNextFeederFromFileModule.action);
			
		}
		else
		{
			//System.out.println("Reservoir (x,y)= " + reservoirActionSelectionModule.action.x() + " " + reservoirActionSelectionModule.action.y() );
			
			subject.robot.pendingActions.add(reservoirActionSelectionModule.action);
		}
				
	}

	
	void addTargetSequence() {

		 
		 
		 
		 
		LinkedList<float[]> _positions = new LinkedList<>();
		LinkedList<Boolean> _rewardHistory = new LinkedList<>();
		LinkedList<float[]> _pcActivations = new LinkedList<>();
		

			
		String[][] strPoints = CSVReader.loadCSV(targetSequenceFileName, "\t", "Place robot file not found");
		
		boolean ignoreLine = true;
		for (String[] s : strPoints){
			
			if(ignoreLine) {
				ignoreLine = false;
				continue;
			}
			
			Float x = Float.parseFloat(s[4]);
			Float y = Float.parseFloat(s[5]);
			Boolean reward = Boolean.parseBoolean(s[8].trim());
			
			
			
			_rewardHistory.add(reward);
			_positions.add(new float[] {x,y});
			
			
			placeCells.run(new Point3f(x,y,0), 10000f);
			float activation_pattern[] = ((Float1dSparsePortMap)placeCells.getOutPort("activation")).getData();
			_pcActivations.add(activation_pattern);
			
		}
		
		reservoir.gather("target_Sequence", _pcActivations, _positions, _rewardHistory);

		 
		 
		 
	 }
	
}
