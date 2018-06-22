package edu.usf.ratsim.model.morris_replay;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Globals;
import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.DrawPanel;
import edu.usf.experiment.display.GuiUtils;
import edu.usf.experiment.display.drawer.Drawer;
import edu.usf.experiment.display.drawer.PathDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.model.SaveModel;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.platform.drawers.PCDrawer;
import edu.usf.platform.drawers.PolarArrowDrawer;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.multiplet.drawers.VDrawer;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

//TODO: some trials are starting from a different place 
public class MorrisReplayModle extends Model implements SaveModel {

	public float step;

	private ModelAsleep modelAsleep;
	private ModelAwake modelAwake;

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

	public MorrisReplayModle(ElementWrapper params, Robot robot) {

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
			modelAwake = new ModelAwake(params, robot, numActions, numPC, QTable, VTable, WTable, step);
			// QTable = new SparseMatrix<Float>(numPC,numActions);
			// WTable = new SparseMatrix<Float>(numPC,numPC);
		}

		modelAsleep = new ModelAsleep(params, robot, numActions, numPC,
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
		((VirtUniverse) UniverseLoader.getUniverse()).setCloseThrs(asleepFoodDistanceThreshold);

		Display display = DisplaySingleton.getDisplay();

		// Execute replay episodes
		for (int r = 0; r < cantReplay; r++) {
			modelAsleep.newEpisode();
			pathDrawer.clearState(); // this is actually equivalent to
										// newEpisode
			fRobot.clearEaten();
			do {
				modelAsleep.run();

				display.updateData();
				display.repaint();
				display.waitUntilDoneRendering();

				Episode.waitNextFrame();

				modelAsleep.move();

			} while (!modelAsleep.doneReplaying());
		}

		// lRobot.setCloseThrs(awakeFoodDistanceThreshold);
		((VirtUniverse) UniverseLoader.getUniverse()).setCloseThrs(awakeFoodDistanceThreshold);

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

	void load() {
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
		Display d = DisplaySingleton.getDisplay();

		d.addPanel(new DrawPanel(300, 300), "panel1", 0, 0, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel2", 1, 0, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel3", 0, 1, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel4", 1, 1, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel5", 0, 2, 1, 1);
		d.addPanel(new DrawPanel(300, 300), "panel6", 1, 2, 1, 1);

		// DrawPanel panel2 = new DrawPanel();
		// panel1.setMinimumSize(new Dimension(300, 300));
		// panel1.setPreferredSize(new Dimension(300, 300));
		// panel1.setBackground(Color.red);

		PolarDataDrawer qSoftMax = new PolarDataDrawer("Q softmax", modelAwake.softmax.probabilities);
		PolarDataDrawer affordances = new PolarDataDrawer("Affordances", modelAwake.affordanceGateModule.gates);
		PolarDataDrawer actionGating = new PolarDataDrawer("2 Actions Gate", modelAwake.twoActionsGateModule.gates);
		resultProbabilities = new PolarDataDrawer("Resulting Probs", modelAwake.twoActionsGateModule.probabilities);
		PCDrawer pcDrawer = new PCDrawer(modelAwake.placeCells.getCells(), modelAwake.placeCells.getActivationPort());
		PCDrawer pcDrawerAsleep = new PCDrawer(modelAwake.getPlaceCells(), modelAsleep.placeCells.getActivationPort());
		pathDrawer = new PathDrawer((LocalizableRobot) lRobot);
		pathDrawer.setColor(Color.red);
		WallDrawer wallDrawer = new WallDrawer(VirtUniverse.getInstance(), 1);
		wallDrawer.setColor(GuiUtils.getHSBAColor(0f, 0f, 0.7f, 1));

		VDrawer vdrawer = new VDrawer(modelAwake.getPlaceCells(), VTable);

		awakePlots.add(qSoftMax);
		awakePlots.add(affordances);
		awakePlots.add(actionGating);
		awakePlots.add(resultProbabilities);
		awakePlots.add(pcDrawer);

		asleepPlots.add(pcDrawerAsleep);

		// asleep plots
		d.addDrawer("universe", "cycle info", new CycleDataDrawer());
		d.addDrawer("universe", "pcsAwake", pcDrawer, 1);
		d.addDrawer("universe", "paths", pathDrawer, 2);
		d.addDrawer("universe", "pcsAsleep", pcDrawerAsleep, 1);

		// awake plots
		d.addDrawer("panel1", "softmax", qSoftMax);
		d.addDrawer("panel2", "affordances", affordances);
		d.addDrawer("panel3", "2 acation gate", actionGating);
		d.addDrawer("panel4", "result", resultProbabilities);

		// always plots
		d.addDrawer("panel5", "wallsPanel5", wallDrawer);
		d.addDrawer("panel5", "ValueF", vdrawer);
		d.addDrawer("panel6", "wallsPanel6", wallDrawer);

		// mapa con paredes + Q plot
		// mapa con paredes + W plot

		turnOnOffPlots(awakePlots, asleepPlots);

	}

}
