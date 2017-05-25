package edu.usf.ratsim.model.tsp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.actionselection.FeederTraveler;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.celllayer.TesselatedPlaceCellLayer;

public class TSPModel extends Model {

	private int numActions;
	private TesselatedPlaceCellLayer placeCells;
	private FeederTraveler feederTraveler;

	public TSPModel() {
	}

	public TSPModel(ElementWrapper params,
			Robot robot) {
		// Get some configuration values for place cells + qlearning
		float PCRadius = params.getChildFloat("PCRadius");
		int numCCCellsPerSide = params.getChildInt("numPCCellsPerSide");
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

		AffordanceRobot aRobot = (AffordanceRobot) robot;
		LocalizableRobot lRobot = (LocalizableRobot) robot;
		
		numActions = aRobot.getPossibleAffordances().size();

		// Create the layers
		placeCells = new TesselatedPlaceCellLayer(
				"PCLayer", lRobot, PCRadius, numCCCellsPerSide, placeCellType,
				xmin, ymin, xmax, ymax);
		addModule(placeCells);
		
		// Module that navigates the feeders
		feederTraveler = new FeederTraveler("Traveler", order, robot);
		// Add in port for dependency
		feederTraveler.addInPort("pc", placeCells.getOutPort("activation"));
		addModule(feederTraveler);

	}

	public void newTrial() {
	}

	public List<PlaceCell> getPlaceCells() {
		return placeCells.getCells();
	}

	public void newEpisode() {
		// TODO Auto-generated method stub
		
	}

	public Map<Integer, Float> getCellActivation() {
		Map<Integer, Float> activation = new LinkedHashMap<Integer, Float>();
		activation.putAll(((Float1dSparsePortMap) placeCells
					.getOutPort("activation")).getNonZero());
		return activation;
	}

}
