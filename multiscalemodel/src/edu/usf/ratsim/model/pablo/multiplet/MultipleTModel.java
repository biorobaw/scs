package edu.usf.ratsim.model.pablo.multiplet;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.Globals;
import edu.usf.experiment.SimulationControl;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DrawPanel;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.pablo.morris_replay.drawers.RuntimesDrawer;
import edu.usf.ratsim.model.pablo.multiplet.drawers.PCDrawerSparsePort;
import edu.usf.ratsim.model.pablo.multiplet.drawers.VDrawer;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

//TODO: some trials are starting from a different place 
public class MultipleTModel extends Model  {

	public float step;

	private MultipleTModelAsleep modelAsleep;
	private MultipleTModelAwake modelAwake;

	public Float2dSparsePort QTable;
	public Float2dSparsePort WTable;
	public Float2dSparsePort VTable;

	int numActions;
	int numPC;

	int cantReplay;
	int episodesPerSession;

	public VirtualRobot lRobot;
	public float awakeFoodDistanceThreshold;
	public float asleepFoodDistanceThreshold;

	private FeederRobot fRobot;

	public MultipleTModel(ElementWrapper params, Robot robot) {

		Globals g = Globals.getInstance();

		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject " + "needs a Localizable Robot");
		lRobot = (VirtualRobot) robot;
		fRobot = (FeederRobot) robot;

		// load parametrs:
		step = params.getChildFloat("step");

		numPC = params.getChildInt("numPC");
		numActions = params.getChildInt("numActions");

		cantReplay = params.getChildInt("cantReplay");

		awakeFoodDistanceThreshold = params.getChildFloat("awakeFoodDistanceThreshold");
		asleepFoodDistanceThreshold = params.getChildFloat("asleepFoodDistanceThreshold");

		// Num actions + 1 for value
		QTable = new Float2dSparsePortMatrix(null, numPC, numActions);
		VTable = new Float2dSparsePortMatrix(null, numPC, 1);
		WTable = new Float2dSparsePortMatrix(null, numPC, numPC);

		Integer loadEpisode = (Integer) g.get("loadEpisode");
		if (loadEpisode != null) {
			load();
		} else {
			modelAwake = new MultipleTModelAwake(params, robot, numActions, numPC, QTable, VTable, WTable, step);
			// QTable = new SparseMatrix<Float>(numPC,numActions);
			// WTable = new SparseMatrix<Float>(numPC,numPC);
		}

		modelAsleep = new MultipleTModelAsleep(params, robot, numActions, numPC,
				(LinkedList<PlaceCell>) modelAwake.placeCells.getCells(), QTable, VTable, WTable, step);

		setDisplay();

	}

	@Override
	public void run() {
		modelAwake.run();
		resultProbabilities.setArrowDirection(modelAwake.chosenAction);

	}

	@Override
	public void newEpisode() {
		modelAwake.newEpisode();
	}

	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();

		pathDrawer.drawOldPaths = true;
		pathDrawer.oldPaths.clear();

		turnOnOffPlots(asleepPlots, awakePlots);

		// Now we are ready to execute replay
		// TODO: fix this, it has no effect currently
		// lRobot.setCloseThrs(asleepFoodDistanceThreshold);
		((VirtUniverse) Universe.getUniverse()).setCloseThrs(asleepFoodDistanceThreshold);

		Display display = Display.getDisplay();

		// Execute replay episodes
		for (int r = 0; r < cantReplay; r++) {
			modelAsleep.newEpisode();
			pathDrawer.endEpisode(); // this is actually equivalent to
										// newEpisode
			fRobot.clearEaten();
			do {
				modelAsleep.run();

				display.updateData();
//				display.repaint();
//				display.waitUntilDoneRendering();

				SimulationControl.waitIfPaused();

				modelAsleep.move();

			} while (!modelAsleep.doneReplaying());
		}

		// lRobot.setCloseThrs(awakeFoodDistanceThreshold);
		((VirtUniverse) Universe.getUniverse()).setCloseThrs(awakeFoodDistanceThreshold);

		// Trick the condition to end simulation
		fRobot.clearEaten();

		pathDrawer.drawOldPaths = false;
		turnOnOffPlots(awakePlots, asleepPlots);

	}

	@Override
	public void newTrial() {
		modelAwake.newTrial();
		modelAsleep.newTrial();
	}

	public List<PlaceCell> getPlaceCells() {
		return modelAwake.getPlaceCells();
	}

	public Map<Integer, Float> getPCActivity() {
		return modelAwake.getCellActivation();
	}

	@Override
	public void load() {
		// String loadPath = (String) g.get("logPath") + "/" + (String)
		// g.get("loadTrial") + "/" + loadEpisode + "/"
		// + (String) g.get("groupName") + "/" + g.get("subName") + "/";
		//
		// if (g.get("loadType").equals("bin")) {
		// QTable = BinaryFile.loadMatrix(loadPath + "QTable.bin");
		// WTable = BinaryFile.loadSparseMatrix(loadPath + "WTable.bin");
		//
		// } else {
		//
		// String[][] qStringValues = CSVReader.loadCSV(loadPath + "QTable.txt",
		// ";");
		// String[][] wStringValues = CSVReader.loadCSV(loadPath + "WTable.txt",
		// ";");
		//
		// for (int i = 0; i < numPC; i++) {
		// for (int j = 0; j < numActions; j++)
		// QTable[i][j] = Float.parseFloat(qStringValues[i][j]);
		// for (int j = 0; j < numPC; j++)
		// WTable[i][j] = Float.parseFloat(wStringValues[i][j]);
		// }
		//
		// }
		//
		//// QTable = new SparseMatrix<Float>(QTableCopy);
		//// WTable = new SparseMatrix<Float>(WTableCopy);
		//
		// System.out.println("loadpath: " + loadPath);
		//
		// modelAwake = new MultipleTModelAwake(params, this, lRobot,
		// numActions, numPC);

	}

	public void save() {
		// Globals g = Globals.getInstance();
		// String logPath = (String) g.get("episodeLogPath");
		//
		// // WTable.getDataAsArray(WTableCopy);
		// // QTable.getDataAsArray(QTableCopy);
		//
		// PrintWriter writer;
		// try {
		// // Save transition table
		// String filename = logPath + "WTable.bin";
		// BinaryFile.saveSparseBinaryMatrix(WTable, filename);
		//
		// // save QTable
		// filename = logPath + "QTable.bin";
		// BinaryFile.saveBinaryMatrix(QTable, filename);
		//
		// // save cells
		// writer = new PrintWriter(logPath + "cellCenters.txt", "UTF-8");
		// for (PlaceCell pc : getPlaceCells()) {
		// Point3f p = pc.getPreferredLocation();
		// writer.println("" + p.x + ";" + p.y + ";");
		// }
		// writer.close();
		//
		// System.out.println("SAVED SUBJECT");
		//
		// } catch (FileNotFoundException | UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	LinkedList<Drawer> awakePlots = new LinkedList<>();
	LinkedList<Drawer> asleepPlots = new LinkedList<>();

	private PathDrawer pathDrawer;

	private PolarDataDrawer resultProbabilities;

	void turnOnOffPlots(LinkedList<Drawer> on, LinkedList<Drawer> off) {
		for (Drawer d : on)
			d.doDraw = true;
		for (Drawer d : off)
			d.doDraw = false;
	}

	void setDisplay() {
		
		//OLD DISPLAY:
		// Add drawing utilities:
		// universe.addDrawingFunction(new DrawPolarGraph("Q softmax", 50, 50, 50, softmax.probabilities, true));
		// universe.addDrawingFunction(new DrawPolarGraph("gated probs", 50, 170, 50, actionGating.probabilities, true));
		// universe.addDrawingFunction(new DrawPolarGraph("biased probs", 50, 290, 50, biasModule.probabilities, true));
		// universe.addDrawingFunction(new DrawPolarGraph("bias ring", 50, 410, 50, biasModule.chosenRing, true));
		
		
		
		
		Display d = Display.getDisplay();

		d.addPanel(new DrawPanel(300, 300), "panel1", 0, 0, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel2", 1, 0, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel3", 0, 1, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel4", 1, 1, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel5", 0, 2, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel6", 1, 2, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel7", 0, 3, 1, 1);


		// DrawPanel panel2 = new DrawPanel();
		// panel1.setMinimumSize(new Dimension(300, 300));
		// panel1.setPreferredSize(new Dimension(300, 300));
		// panel1.setBackground(Color.red);

		
		
		//ACTION SELECTION PROCESS
		PolarDataDrawer qSoftMax = new PolarDataDrawer("Q softmax", modelAwake.softmax.probabilities);
		PolarDataDrawer affordances = new PolarDataDrawer("Affordances", modelAwake.affordanceGateModule.gates);
		PolarDataDrawer actionGating = new PolarDataDrawer("2 Actions Gate", modelAwake.twoActionsGateModule.gates);
		resultProbabilities = new PolarDataDrawer("Resulting Probs", modelAwake.twoActionsGateModule.probabilities);

		
		//TIME SERIES DATA
		RuntimesDrawer runtimes = new RuntimesDrawer(10, 0, 800);
		runtimes.doLines = false;
		pathDrawer = new PathDrawer((LocalizableRobot) lRobot);
		pathDrawer.setColor(Color.red);
		
		
		//UNIVERSE RELATED DRAWERS
		RobotDrawer rDrawer = new RobotDrawer((GlobalCameraUniverse)Universe.getUniverse());
		WallDrawer wallDrawer = new WallDrawer(VirtUniverse.getInstance(), 1);
		wallDrawer.setColor(GuiUtils.getHSBAColor(0f, 0f, 0f, 1));
				
		
		
		
		//RASTERS DRAWERS
		PCDrawerSparsePort pcDrawer 		= new PCDrawerSparsePort(modelAwake.placeCells.getCells(), modelAwake.placeCells.getActivationPort());
		PCDrawerSparsePort pcDrawerAsleep = new PCDrawerSparsePort(modelAwake.getPlaceCells(), modelAsleep.placeCells.getActivationPort());
		
		VDrawer vdrawer = new VDrawer(modelAwake.getPlaceCells(), VTable);
		vdrawer.distanceOption = 0; // use pc radis to draw PCs

		
		
		//DEFINE SET OF AWAKE/ASLEEP PLOTS
		awakePlots.add(qSoftMax);
		awakePlots.add(affordances);
		awakePlots.add(actionGating);
		awakePlots.add(resultProbabilities);
		awakePlots.add(pcDrawer);

		asleepPlots.add(pcDrawerAsleep);
		


		// ADD ALL PLOTS TO THE PANELS
		d.addDrawer("universe", "cycle info", new CycleDataDrawer());
		

		// awake plots
		d.addDrawer("panel1", "softmax", qSoftMax);
		d.addDrawer("panel2", "affordances", affordances);
		d.addDrawer("panel3", "2 acation gate", actionGating);
		d.addDrawer("panel4", "result", resultProbabilities);

		// always plots
		d.addDrawer("panel5", "wallsPanel5", wallDrawer);
		d.addDrawer("panel5", "ValueF", vdrawer);
		
		
		
		d.addDrawer("panel6", "pcsAwake" , pcDrawer);
		d.addDrawer("panel6", "pcsAsleep", pcDrawerAsleep);
		d.addDrawer("panel6", "wallsPanel6", wallDrawer);
		d.addDrawer("panel6", "paths", pathDrawer, 2);
		
		d.addDrawer("panel6", "p6 robot", rDrawer);
		d.addDrawer("panel5", "p5 robot", rDrawer);
		d.addDrawer("panel7", "runtimes", runtimes);
		
		

		// mapa con paredes + Q plot
		// mapa con paredes + W plot

		turnOnOffPlots(awakePlots, asleepPlots);

	}

}