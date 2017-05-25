package edu.usf.ratsim.model.multiplet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.Globals;
import edu.usf.experiment.model.SaveModel;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePort;
import edu.usf.micronsl.port.twodimensional.sparse.Float2dSparsePortMatrix;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.vlwsim.robot.VirtualRobot;

public class MultipleTModel extends Model implements SaveModel {

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
			modelAwake = new MultipleTModelAwake(params, robot, numPC, QTable, WTable, step);
			// QTable = new SparseMatrix<Float>(numPC,numActions);
			// WTable = new SparseMatrix<Float>(numPC,numPC);
		}

		modelAsleep = new MultipleTModelAsleep(params, robot, numActions, numPC,
				(LinkedList<PlaceCell>) modelAwake.placeCells.getCells(), QTable, WTable, step);
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
	public void simRun() {
		modelAwake.simRun();

		// Replay after eating
		// But first wait one extra cycle
		if (fRobot.hasRobotEaten()) {
			// Now we are ready to execute replay
			// TODO: fix this, it has no effect currently
			lRobot.setCloseThrs(asleepFoodDistanceThreshold);

			// Execute replay episodes
			for (int r = 0; r < cantReplay; r++) {
				modelAsleep.newEpisode();
				fRobot.clearEaten();
				do {
					modelAsleep.simRun();
				} while (!fRobot.hasFoundFood() && modelAsleep.getMaxActivation() > replayThres);

			}

			lRobot.setCloseThrs(awakeFoodDistanceThreshold);
			
			// Trick the condition to end simulation
			fRobot.clearEaten();
		}
		
		
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

	
	public List<PlaceCell> getPlaceCells() {
		return modelAwake.getPlaceCells();
	}

	public Map<Integer, Float> getPCActivity() {
		return modelAwake.getCellActivation();
	}

}
