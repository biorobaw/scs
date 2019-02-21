package edu.usf.ratsim.model.pablo.morris_replay;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
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
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.simulation.CycleDataDrawer;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.twodimensional.sparse.Entry;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.platform.drawers.PCDrawer;
import edu.usf.platform.drawers.PolarDataDrawer;
import edu.usf.ratsim.model.pablo.morris_replay.drawers.RuntimesDrawer;
import edu.usf.ratsim.model.pablo.multiplet.drawers.VDrawer;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

//TODO: some trials are starting from a different place 
public class MorrisReplayModel extends Model  {

	public float step;

	private ModelAsleep modelAsleep;
	private ModelAwake modelAwake;

	public Float2dSparsePort QTable;
	public Float2dSparsePort WTable;
	public Float2dSparsePort VTable;

	int numActions;
	int numPCPerSide;
	int numPC;

	int cantReplay;
	int episodesPerSession;

	public VirtualRobot lRobot;
	public float awakeFoodDistanceThreshold;
	public float asleepFoodDistanceThreshold;

	private FeederRobot fRobot;

	public MorrisReplayModel(ElementWrapper params, Robot robot) {

		Globals g = Globals.getInstance();

		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject " + "needs a Localizable Robot");
		lRobot = (VirtualRobot) robot;
		fRobot = (FeederRobot) robot;

		// load parametrs:
		step = params.getChildFloat("step");

		numPCPerSide =  params.getChildInt("numPCPerSide");
		numPC = numPCPerSide*numPCPerSide;
		numActions = params.getChildInt("numActions");

		cantReplay = params.getChildInt("cantReplay");

		awakeFoodDistanceThreshold = params.getChildFloat("awakeFoodDistanceThreshold");
		asleepFoodDistanceThreshold = params.getChildFloat("asleepFoodDistanceThreshold");

		// Num actions + 1 for value
		QTable = new Float2dSparsePortMatrix(null, numPC , numActions);
		VTable = new Float2dSparsePortMatrix(null, numPC, 1);
		WTable = new Float2dSparsePortMatrix(null, numPC, numPC);


		modelAwake = new ModelAwake(params, robot, numActions, numPC, QTable, VTable, WTable, step);
		modelAsleep = new ModelAsleep(params, robot, numActions, numPC,
				(ArrayList<PlaceCell>) modelAwake.placeCells.getCells(), QTable, VTable, WTable, step);
		

		setDisplay();

	}

	@Override
	public void run() {
		modelAwake.run();
		resultProbabilities.setArrowDirection(modelAwake.chosenAction);

	}

	@Override
	public void newEpisode() {
		
		save();
		WTable.clear();
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

		Display display = DisplaySingleton.getDisplay();

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

				Episode.waitNextStep();

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
		String loadPath = Globals.getInstance().get("loadPath").toString();
		String snapshot = Globals.getInstance().get("loadSnapshot").toString();
		
		RandomSingleton.load(snapshot);
//		float[][] c = BinaryFile.loadMatrix(loadPath+"/pcCenters.bin");
		Map<Entry,Float> w = BinaryFile.loadSparseMatrix(snapshot+"WTable.bin");
		float[][] q = BinaryFile.loadMatrix(snapshot+"QTable.bin");
		float[][] v = BinaryFile.loadMatrix(snapshot+"VTable.bin");
		
		//replace pcs with loaded ones
//		modelAsleep.setPCcenters(c);
//		modelAwake.setPCcenters(c);
		
		//restore w
		WTable.clear();
		for(Entry e : w.keySet())
			WTable.set(e.i, e.j, w.get(e));
		
		//restore v
		VTable.clear();
		for(int i=0;i<numPC;i++) VTable.set(i, 0, v[i][0]);
		
		//restore q
		QTable.clear();
		for(int i=0;i<numPC;i++)
			for(int j=0;j<numActions;j++) QTable.set(i, j, q[i][j]);
		

	}

//	static boolean savedOnce = false;
	@Override
	public void save() {
		
		
		
		//save list of PCs (if necessary)
		//save random state
		//save W matrix - as sparse binary matrix
		//save Q matrix - as binary matrix
		//save V matrix - as binary matrix
		Globals g = Globals.getInstance();
		String savePath = g.get("savePath").toString();
		String snapshotPath = g.get("saveSnapshotPath").toString();
		
		 File directory = new File(snapshotPath);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		
		
		//save model invariants
		//save place cell centers
//		if(!savedOnce){
//			
//			List<PlaceCell> pcs = getPlaceCells();
//			float pcCenters[][] = new float[pcs.size()][2];
//			for(int i=0;i<pcs.size();i++) {
//				pcCenters[i][0] = (float)pcs.get(i).getPreferredLocation().x;
//				pcCenters[i][1] = (float)pcs.get(i).getPreferredLocation().y;
//			}
//			BinaryFile.saveBinaryMatrix(pcCenters, pcs.size(), 2, savePath + "pcCenters.bin");
//			savedOnce=true;
//		}
		
		
		//save variables of the model
		
		
		//save Random state
		RandomSingleton.save(snapshotPath);
		//save W, V and Q
		BinaryFile.saveSparseBinaryMatrix(WTable.getNonZero(), WTable.getNRows(), WTable.getNCols(), snapshotPath+"WTable.bin");
		BinaryFile.saveBinaryMatrix(QTable.getNonZero(), QTable.getNRows(), QTable.getNCols(), snapshotPath+"QTable.bin", false);//this should not be sparse
		BinaryFile.saveBinaryMatrix(VTable.getNonZero(), VTable.getNRows(), VTable.getNCols(), snapshotPath+"VTable.bin", false);//this should not be sparse
		
		

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
		RuntimesDrawer runtimes = new RuntimesDrawer(Integer.parseInt(Globals.getInstance().get("learningEpisodes").toString()), 0, 800);
		runtimes.doLines = false;
		pathDrawer = new PathDrawer((LocalizableRobot) lRobot);
		pathDrawer.setColor(Color.red);
		
		
		//UNIVERSE RELATED DRAWERS
		RobotDrawer rDrawer = new RobotDrawer((GlobalCameraUniverse)Universe.getUniverse());
		WallDrawer wallDrawer = new WallDrawer(VirtUniverse.getInstance(), 1);
		wallDrawer.setColor(GuiUtils.getHSBAColor(0f, 0f, 0f, 1));
		
		
		//RASTERS DRAWERS
		PCDrawer pcDrawer 		= new PCDrawer(modelAwake.placeCells.getCells(), modelAwake.placeCells.getActivationPort());
		PCDrawer pcDrawerAsleep = new PCDrawer(modelAwake.getPlaceCells(), modelAsleep.placeCells.getActivationPort());
		

		VDrawer vdrawer = new VDrawer(modelAwake.getPlaceCells(), VTable);
//		vdrawer.setRadius(10);

		
		
		
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
