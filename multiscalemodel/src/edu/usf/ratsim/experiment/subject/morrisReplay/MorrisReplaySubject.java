package edu.usf.ratsim.experiment.subject.morrisReplay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.utils.BinaryFile;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.experiment.subject.morrisReplay.gui.DrawPathMatrix;
import edu.usf.ratsim.experiment.subject.morrisReplay.gui.DrawVFromQ;
import edu.usf.ratsim.experiment.subject.morrisReplay.gui.GuiUtils;
import edu.usf.ratsim.experiment.universe.virtual.CanvasRecorder;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import edu.usf.ratsim.experiment.universe.virtual.drawingUtilities.DrawPolarGraph;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class MorrisReplaySubject extends SubjectOld {

	public float step;

	private MorrisReplayModelAsleep modelAsleep;
	private MorrisReplayModelAwake modelAwake;

	public Float2dSparsePort QTable;
	public Float2dSparsePort WTable;
	
	int numActions;
	int numPCPerSide;

	int cantReplay;
	int episodesPerSession;
	float replayThres;
	
	boolean matrixCheckPointing = false;

	public VirtualRobot lRobot;
	public float awakeFoodDistanceThreshold;
	public float asleepFoodDistanceThreshold;
	
	CanvasRecorder recorder = null;

	DrawPathMatrix wDrawer = null; 
	
	public MorrisReplaySubject(String name, String group, ElementWrapper params, RobotOld robot) {
		super(name, group, params, robot);

		
		GuiUtils.setWorldCoordinates(-1, -1, 1, 1);
		
		Globals g = Globals.getInstance();
		
		if(VirtUniverse.getInstance().display) recorder = new CanvasRecorder(VirtUniverse.getInstance().frame.topViewPanel,"");


		lRobot = (VirtualRobot) robot;

		// load parametrs:
		step = params.getChildFloat("step");

		numPCPerSide = params.getChildInt("numPCPerSide");
		int numPC = numPCPerSide*numPCPerSide;
		numActions = params.getChildInt("numActions");

		replayThres = params.getChildFloat("replayThres");
		cantReplay = params.getChildInt("cantReplay");

		awakeFoodDistanceThreshold = params.getChildFloat("awakeFoodDistanceThreshold");
		asleepFoodDistanceThreshold = params.getChildFloat("asleepFoodDistanceThreshold");
		
		matrixCheckPointing = params.getChildBoolean("matrixCheckPointing");

		// Num actions + 1 for value
		QTable = new Float2dSparsePortMatrix(null, numPC, numActions+1);
		WTable = new Float2dSparsePortMatrix(null, numPC, numPC);

		Integer loadEpisode = (Integer) g.get("loadEpisode");
		if (loadEpisode != null) {
//			String loadPath = (String) g.get("logPath") + "/" + (String) g.get("loadTrial") + "/" + loadEpisode + "/"
//					+ (String) g.get("groupName") + "/" + g.get("subName") + "/";
//
//			if (g.get("loadType").equals("bin")) {
//				QTable = BinaryFile.loadMatrix(loadPath + "QTable.bin");
//				WTable = BinaryFile.loadSparseMatrix(loadPath + "WTable.bin");
//
//			} else {
//
//				String[][] qStringValues = CSVReader.loadCSV(loadPath + "QTable.txt", ";");
//				String[][] wStringValues = CSVReader.loadCSV(loadPath + "WTable.txt", ";");
//
//				for (int i = 0; i < numPC; i++) {
//					for (int j = 0; j < numActions; j++)
//						QTable[i][j] = Float.parseFloat(qStringValues[i][j]);
//					for (int j = 0; j < numPC; j++)
//						WTable[i][j] = Float.parseFloat(wStringValues[i][j]);
//				}
//
//			}
//
//			// QTable = new SparseMatrix<Float>(QTableCopy);
//			// WTable = new SparseMatrix<Float>(WTableCopy);
//
//			System.out.println("loadpath: " + loadPath);
//
//			modelAwake = new MultipleTModelAwake(params, this, lRobot, numActions, numPC);
		} else {
			modelAwake = new MorrisReplayModelAwake(params, this, lRobot, numActions, numPCPerSide);
			// QTable = new SparseMatrix<Float>(numPC,numActions);
			// WTable = new SparseMatrix<Float>(numPC,numPC);
		}

		modelAsleep = new MorrisReplayModelAsleep(params, this, lRobot, numActions, numPCPerSide,
				(LinkedList<PlaceCell>) modelAwake.placeCells.getCells());
		
		
		
		VirtUniverse universe = VirtUniverse.getInstance();
		universe.addDrawingFunction(new DrawVFromQ(QTable, 8, getPlaceCells()));
		wDrawer = new DrawPathMatrix(WTable, getPlaceCells(), 0);
		wDrawer.active = true;
		universe.addDrawingFunction(wDrawer);
		
	}

	public void save() {
		Globals g = Globals.getInstance();
		String logPath = (String) g.get("episodeLogPath");
		
		saveW();
		saveQ("AfterReplay");

		PrintWriter writer;
		try {

			// save cells
			writer = new PrintWriter(logPath + "cellCenters.txt", "UTF-8");
			for (PlaceCell pc : getPlaceCells()) {
				Point3f p = pc.getPreferredLocation();
				writer.println("" + p.x + ";" + p.y + ";");
			}
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void saveW(){
		Globals g = Globals.getInstance();
		String logPath = (String) g.get("episodeLogPath");

		// Save transition table
		String filename = logPath + "WTable.bin";
		BinaryFile.saveSparseBinaryMatrix(WTable.getData(), filename);

	}
	
	public void saveQ(String suffix){
		Globals g = Globals.getInstance();
		String logPath = (String) g.get("episodeLogPath");

		String filename = logPath + "QTable"+suffix + ".bin";
		BinaryFile.saveBinaryMatrix(QTable.getData(), filename);

	}
	
	
	
	@Override
	public void stepCycle() {

		modelAwake.simRun();
		
//		VirtUniverse vu = VirtUniverse.getInstance();
//		vu.render(true);
//		
//		if(recorder!=null) recorder.record();
		
	}
	
	@Override
	public void endEpisode() {
		// TODO Auto-generated method stub
		super.endEpisode();
		
		if(recorder!=null) recorder.stopRecording();
		
		if(matrixCheckPointing) saveQ("BeforeReplay");
		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Globals g = Globals.getInstance();
		int[] sleepValues = (int[])g.get("sleepValues");

		lRobot.setCloseThreshold(asleepFoodDistanceThreshold);

		System.out.println("Doing Replay... ");
		wDrawer.active = true;
		// Execute replay episodes
		for (int r = 0; r < cantReplay; r++) {
//			System.out.println("REPLAY EPISODE: " + r);
			
			setHasEaten(false);
			modelAsleep.newEpisode();
			
			
			VirtUniverse.getInstance().render(true);
			try {
				Thread.sleep(50);;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			int iterationCount = 0;
			do {
				
				//logging must be done at this point
				
				VirtUniverse.getInstance().render(true);
				
				try {
					Thread.sleep(sleepValues[(int)g.get("simulationSpeed")]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				modelAsleep.simRun();
				iterationCount++;
				
			} while (!modelAsleep.subAte.subAte() && modelAsleep.getMaxActivation() > replayThres && iterationCount < 2000 );
			
			
			
			

		}
		
		System.out.println("Done with replay.");

		lRobot.setCloseThreshold(awakeFoodDistanceThreshold);
				
		if(matrixCheckPointing) save();
		
//		wDrawer.active = false;
		
	}
	
	@Override
	public boolean hasEaten() {
		// TODO Auto-generated method stub
		return modelAwake.subAte.subAte();
	}

	@Override
	public List<Affordance> getPossibleAffordances() {
		throw new NotImplementedException();

	}

	@Override
	public float getMinAngle() {
		throw new NotImplementedException();
	}

	@Override
	public void newEpisode() {
		
		VirtUniverse vu = VirtUniverse.getInstance();
		vu.render(true);
		vu.render(true);

		
//		try {
//		 new BufferedReader(new InputStreamReader(System.in)).readLine();;
//
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		
		if(recorder!=null) recorder.startRecording();
		super.newEpisode();
		modelAwake.newEpisode();
	}
	

	@Override
	public void newTrial() {
		modelAwake.newTrial();
		modelAsleep.newTrial();
	}

	@Override
	public Affordance getHypotheticAction(Point3f pos, float theta, int intention) {
		return null;
	}

	@Override
	public void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant) {
		throw new NotImplementedException();
	}

	@Override
	public void setExplorationVal(float val) {
		throw new NotImplementedException();
	}

	@Override
	public float getStepLenght() {
		return step;
	}

	@Override
	public Map<Float, Float> getValue(Point3f point, int intention, float angleInterval, float distToWall) {
		throw new NotImplementedException();
	}

	public List<PlaceCell> getPlaceCells() {
		return modelAwake.getPlaceCells();
	}

	@Override
	public void deactivateHPCLayersProportion(LinkedList<Integer> indexList, float proportion) {
		throw new NotImplementedException();
	}

	@Override
	public void remapLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}

	public Map<Integer, Float> getPCActivity() {
		return modelAwake.getCellActivation();
	}

	@Override
	public float getValueEntropy() {
		throw new NotImplementedException();
	}

	@Override
	public void reactivateHPCLayers(LinkedList<Integer> indexList) {
		throw new NotImplementedException();
	}

	@Override
	public Affordance getForwardAffordance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Affordance getLeftAffordance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Affordance getRightAffordance() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void changeRewardValue(float r){
		modelAsleep.rewardModule.foodReward = r;
		modelAwake.rewardModule.foodReward =r;
	}

}
