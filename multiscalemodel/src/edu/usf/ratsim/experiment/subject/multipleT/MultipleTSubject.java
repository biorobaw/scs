package edu.usf.ratsim.experiment.subject.multipleT;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class MultipleTSubject extends SubjectOld {

	public float step;

	private MultipleTModelAsleep modelAsleep;
	private MultipleTModelAwake modelAwake;

	public Float2dSparsePort QTable;
	public Float2dSparsePort WTable;
	
	int numActions;
	int numPC;

	int cantReplay;
	int episodesPerSession;
	float replayThres;

	public VirtualRobot lRobot;
	public float awakeFoodDistanceThreshold;
	public float asleepFoodDistanceThreshold;

	public MultipleTSubject(String name, String group, ElementWrapper params, RobotOld robot) {
		super(name, group, params, robot);

		Globals g = Globals.getInstance();

		if (!(robot instanceof LocalizableRobot))
			throw new RuntimeException("TSPSubject " + "needs a Localizable Robot");
		lRobot = (VirtualRobot) robot;

		// load parametrs:
		step = params.getChildFloat("step");

		numPC = params.getChildInt("numPC");
		numActions = params.getChildInt("numActions");

		replayThres = params.getChildFloat("replayThres");
		cantReplay = params.getChildInt("cantReplay");

		awakeFoodDistanceThreshold = params.getChildFloat("awakeFoodDistanceThreshold");
		asleepFoodDistanceThreshold = params.getChildFloat("asleepFoodDistanceThreshold");

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
			modelAwake = new MultipleTModelAwake(params, this, lRobot, numActions, numPC);
			// QTable = new SparseMatrix<Float>(numPC,numActions);
			// WTable = new SparseMatrix<Float>(numPC,numPC);
		}

		modelAsleep = new MultipleTModelAsleep(params, this, lRobot, numActions, numPC,
				(LinkedList<PlaceCell>) modelAwake.placeCells.getCells());
	}

	public void save() {
//		Globals g = Globals.getInstance();
//		String logPath = (String) g.get("episodeLogPath");
//
//		// WTable.getDataAsArray(WTableCopy);
//		// QTable.getDataAsArray(QTableCopy);
//
//		PrintWriter writer;
//		try {
//			// Save transition table
//			String filename = logPath + "WTable.bin";
//			BinaryFile.saveSparseBinaryMatrix(WTable, filename);
//
//			// save QTable
//			filename = logPath + "QTable.bin";
//			BinaryFile.saveBinaryMatrix(QTable, filename);
//
//			// save cells
//			writer = new PrintWriter(logPath + "cellCenters.txt", "UTF-8");
//			for (PlaceCell pc : getPlaceCells()) {
//				Point3f p = pc.getPreferredLocation();
//				writer.println("" + p.x + ";" + p.y + ";");
//			}
//			writer.close();
//
//			System.out.println("SAVED SUBJECT");
//
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	@Override
	public void stepCycle() {
		setHasEaten(false);
		clearTriedToEAt();

		modelAwake.simRun();

		// Replay after eating
		// But first wait one extra cycle
		if (hasEaten()) {
			// Now we are ready to execute replay
			lRobot.setCloseThreshold(asleepFoodDistanceThreshold);

			// Execute replay episodes
			for (int r = 0; r < cantReplay; r++) {
				modelAsleep.newEpisode();
				setHasEaten(false);
				clearTriedToEAt();
				do {
					modelAsleep.simRun();
				} while (!hasEaten() && modelAsleep.getMaxActivation() > replayThres);

			}

			lRobot.setCloseThreshold(awakeFoodDistanceThreshold);
			
			// Trick the condition to end simulation
			setHasEaten(true);
		}
		
		
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

}
