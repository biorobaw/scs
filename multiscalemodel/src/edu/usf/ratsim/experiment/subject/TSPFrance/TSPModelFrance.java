package edu.usf.ratsim.experiment.subject.TSPFrance;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
	static final int DEFAULT_INDEX = 0; // 0 = CPU, > 0 = GPU
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
	
	private int episode;
	private int call_number = 0;
	PuckRobot robot;
	
	static 
	{
		System.loadLibrary("vcomp120");
		System.loadLibrary("msvcr120");
		System.loadLibrary("msvcp120");

		System.loadLibrary("vcruntime140");
		System.loadLibrary("concrt140");
		System.loadLibrary("msvcp140");

		System.loadLibrary("vcomp140");

		System.loadLibrary("tbb");
		System.loadLibrary("tbbmalloc");

		System.loadLibrary("libimalloc");
		System.loadLibrary("libiomp5md");
		System.loadLibrary("mkl_core");
		System.loadLibrary("mkl_intel_thread");
		System.loadLibrary("mkl_sequential");
		System.loadLibrary("mkl_tbb_thread");
		System.loadLibrary("mkl_def");
		System.loadLibrary("mkl_avx");
		System.loadLibrary("mkl_avx2");
		System.loadLibrary("mkl_avx512");
		System.loadLibrary("mkl_avx512_mic");

		System.loadLibrary("mkl_mc");
		System.loadLibrary("mkl_mc3");
		System.loadLibrary("mkl_rt");

		System.loadLibrary("mkl_vml_avx");
		System.loadLibrary("mkl_vml_avx2");
		System.loadLibrary("mkl_vml_avx512");
		System.loadLibrary("mkl_vml_avx512_mic");
		System.loadLibrary("mkl_vml_cmpt");
		System.loadLibrary("mkl_vml_def");
		System.loadLibrary("mkl_vml_mc");
		System.loadLibrary("mkl_vml_mc2");
		System.loadLibrary("mkl_vml_mc3");

		System.loadLibrary("hdf5");

		System.loadLibrary("icudt56");
		System.loadLibrary("icuin56");
		System.loadLibrary("icuio56");
		System.loadLibrary("icuuc56");
		System.loadLibrary("zlib1");
		System.loadLibrary("libexpat");

		System.loadLibrary("libmwfl");
		System.loadLibrary("libmwfoundation_usm");
		System.loadLibrary("libmwi18n");
		System.loadLibrary("libmwresource_core");

		System.loadLibrary("libut");
		System.loadLibrary("libmat");
		System.loadLibrary("libmx");

		System.loadLibrary("cudart64_90");
		System.loadLibrary("cublas64_90");
		System.loadLibrary("curand64_90");

		System.loadLibrary("boost_chrono-vc120-mt-1_56");
		System.loadLibrary("boost_date_time-vc120-mt-1_56");
		System.loadLibrary("boost_filesystem-vc120-mt-1_56");
		System.loadLibrary("boost_log-vc120-mt-1_56");
		System.loadLibrary("boost_regex-vc120-mt-1_56");
		System.loadLibrary("boost_serialization-vc120-mt-1_56");
		System.loadLibrary("boost_signals-vc120-mt-1_56");
		System.loadLibrary("boost_thread-vc120-mt-1_56");
		System.loadLibrary("boost_system-vc120-mt-1_56");


		System.loadLibrary("boost_chrono-vc140-mt-1_62");
		System.loadLibrary("boost_date_time-vc140-mt-1_62");
		System.loadLibrary("boost_filesystem-vc140-mt-1_62");
		System.loadLibrary("boost_thread-vc140-mt-1_62");
		System.loadLibrary("boost_program_options-vc140-mt-1_62");
		System.loadLibrary("boost_serialization-vc140-mt-1_62");
		System.loadLibrary("boost_system-vc140-mt-1_62");
		System.loadLibrary("boost_zlib-vc140-mt-1_62");
		System.loadLibrary("boost_bzip2-vc140-mt-1_62");
		System.loadLibrary("boost_log-vc140-mt-1_62");
		System.loadLibrary("boost_regex-vc140-mt-1_62");
		System.loadLibrary("boost_log_setup-vc140-mt-1_62");
		System.loadLibrary("boost_iostreams-vc140-mt-1_62");
		System.loadLibrary("boost_mpi-vc140-mt-1_62");
		
		System.loadLibrary("Backend");
		System.loadLibrary("GPU");
		System.loadLibrary("CPU");

		System.loadLibrary("Helper");
		System.loadLibrary("Core");
		System.loadLibrary("Initializer");
		System.loadLibrary("Loop");
		System.loadLibrary("Measurement");
		System.loadLibrary("Mutator");
		System.loadLibrary("Reservoir");
		System.loadLibrary("Scheduler");
		System.loadLibrary("Simulator");
		System.loadLibrary("Model");

		System.loadLibrary("Network");

		System.loadLibrary("Engine");
		System.loadLibrary("Remote");
		System.loadLibrary("Distributed");
		System.loadLibrary("Local");
	
		System.loadLibrary("ViewModel");
		System.loadLibrary("TRN4CPP");
		System.loadLibrary("TRN4JAVA");
	}	

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
		
		TRN4JAVA.Engine.Events.Trained.install(new TRN4JAVA.Engine.Events.Trained()
		{
			@Override
			public void callback(final long id)
			{
				System.out.println("Simulation " + id + " is trained");
				reservoir.onTrained(id);
			}
		});
		TRN4JAVA.Engine.Events.Tested.install(new TRN4JAVA.Engine.Events.Tested()
		{
			@Override
			public void callback(final long id)
			{
				System.out.println("Simulation " + id + " is tested");
				reservoir.onTested(id);
			}
		});
		TRN4JAVA.Engine.Events.Primed.install(new TRN4JAVA.Engine.Events.Primed()
		{
			@Override
			public void callback(final long id)
			{
				System.out.println("Simulation " + id + " is primed");
				reservoir.onPrimed(id);
			}
		});
		TRN4JAVA.Engine.Events.Completed.install(new TRN4JAVA.Engine.Events.Completed()
		{
			@Override
			public void callback()
			{
				System.out.println("Simulations completed");
			}
		});
		TRN4JAVA.Engine.Events.Allocated.install(new TRN4JAVA.Engine.Events.Allocated()
		{
			@Override
			public void callback(final long id, final int rank)
			{
				System.out.println("Simulation " + id + " allocated on processor rank " + rank);
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
				TRN4JAVA.Engine.Backend.Local.initialize(new int []{index});
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
				TRN4JAVA.Engine.Backend.Remote.initialize(host, port);
			}
			break;
			
			default :
			      throw new IllegalArgumentException("Invalid backend type :" + backendType);
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
				"PCLayer", PCRadius, numPCellsPerSide, "ExponentialNoThresholding",
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
		int time_budget = params.getChildInt("timeBudget");
		int snippets_size = params.getChildInt("snippetsSize");
		int rows = params.getChildInt("rows");
		int cols = params.getChildInt("cols");
		float sigma = params.getChildFloat("sigma");
		float radius = params.getChildFloat("radius");
		float scale = params.getChildFloat("scale");
		int preamble = params.getChildInt("preamble");
		int stimulus_size = numPCellsPerSide * numPCellsPerSide;
		
		//float responses[][] = new float[stimulus_size][rows * cols];
		float response[] = new float[stimulus_size * rows * cols];
		
		for (int row = 0; row < rows; row++)
		{
			float y = ((row) / (float)(rows-1))*(ymax - ymin) + ymin;
			
			for (int col = 0; col < cols; col++)
			{
				float x = (col/ (float)(cols-1))*(xmax - xmin) + xmin;
					
			//	System.out.println("x, y = " + x + ", " + y);
				float activation[] = placeCells.getActivationValues(new Point3f(x, y, 0));
				for (int pc = 0; pc < stimulus_size; pc++)
				{
					//responses[pc][row * cols + col] = activation[pc];
					response[pc * rows * cols + row * cols + col] = activation[pc];
				}
					//
			}
		
		}
		
		//test_response( -0.3f, -1.0f, response, stimulus_size, rows, cols);
	//	
	
		
		reservoir = new Reservoir(ID,
				stimulus_size, reservoir_size, leak_rate, initial_state_scale, learning_rate,
				snippets_size, time_budget,
				rows, cols, xmin, xmax, ymin, ymax, response, sigma, radius, scale,
				preamble );
		
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
		
		
		
		
		episode = 0;
	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	@Override
	public void newEpisode() {
		episode++;
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
	
	
		call_number++;
		if (call_number > 3)
		{
			Boolean finishedAction = finishReservoirAction.get();
		
		/*if (finishedAction)
		{*/
			Boolean ate = ((Bool0dPort)subAte.getOutPort("subAte")).get();
			if(ate) System.out.println("subject ate? "+ ate);
			float activation_pattern[] = ((Float1dSparsePortMap)placeCells.getOutPort("activation")).getData();
			//((Float1dSparsePortMap)getInPort("placeCells")).getData()
			Point3f pos = ((Point3fPort)posModule.getOutPort("position")).get();
			float estimated_position[] = {pos.x, pos.y};
		
			boolean append = true;
			if (!posHistory.isEmpty())
			{
				float last_position[] = posHistory.getLast();
		
				float dx2 = (float)Math.pow(last_position[0] - estimated_position[0], 2);
				float dy2 = (float)Math.pow(last_position[1] - estimated_position[1], 2);
				float d = (float)Math.sqrt(dx2 + dy2);
				append = d > 0.0f;
			}
			
			if (append)
			{
			
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
		
	
		//System.out.println(subject.robot.pendingActions.size() + " ");
		if (episode % 2 == 0)
		{
			//System.out.println("RESERVOIR ACTION SELECTED");
	
			subject.robot.pendingActions.add(reservoirActionSelectionModule.action);
		}
		else
		{
			//System.out.println("RANDOM FEEDER ACTION SELECTED");
			subject.robot.pendingActions.add(randomOrClosestFeederTaxicActionModule.action);
		}
				
	}

}
