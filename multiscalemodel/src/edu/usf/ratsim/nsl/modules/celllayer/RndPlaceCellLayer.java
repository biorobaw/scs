package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCell;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ProportionalPlaceCell;
import edu.usf.ratsim.nsl.modules.cell.WallExponentialPlaceCell;

/**
 * This layer locates place cells uniformly inside a given square in the
 * environment. It can be used with proportional, exponential and wall
 * exponential place cells.
 * 
 * A selected proportion of place cells are located near feeders instead of
 * uniformly in the environment.
 * 
 * @author Martin Llofriu
 *
 */
public class RndPlaceCellLayer extends Module {

	/**
	 * The layer's cells current activation
	 */
	public float[] activation;

	/**
	 * The list of place cells
	 */
	private List<PlaceCell> cells;

	/**
	 * A pointer to the robot. This is used to get the robot's current location
	 */
	private LocalizableRobot lRobot;

	/**
	 * The output port. A sparse port is used for efficiency.
	 */
	private Float1dSparsePortMap activationPort;

	private WallRobot wRobot;

	/**
	 * Create all cells in the layer
	 * 
	 * @param name
	 *            The module's name
	 * @param robot
	 *            A robot to provide localization information
	 * @param placeRadius
	 *            The radius of the created place cells
	 * @param numCells
	 *            The number of cells in the layer
	 * @param placeCellType
	 *            The type of place cells. ProportionalPlaceCell,
	 *            ExponentialPlacCell and WallExponantialPlaceCell are supported
	 * @param xmin
	 *            The minimum x value of the box in which place cells are
	 *            located
	 * @param ymin
	 *            The minimum y value of the box in which place cells are
	 *            located
	 * @param xmax
	 *            The maximum x value of the box in which place cells are
	 *            located
	 * @param ymax
	 *            The maximum y value of the box in which place cells are
	 *            located
	 * @param goals
	 *            The list of possible goals. This is used to locate place cells
	 *            near goals.
	 * @param nearGoalProb
	 *            The probability of a place cell being place near a goal
	 *            instead of a generic place
	 */
	public RndPlaceCellLayer(String name, Robot robot, float placeRadius, int numCells, String placeCellType,
			float xmin, float ymin, float xmax, float ymax, List<Feeder> goals, float nearGoalProb) {
		super(name);

		if (!(placeCellType.equals("ProportionalPlaceCell") || placeCellType.equals("ExponentialPlaceCell")
				|| placeCellType.equals("WallExponentialPlaceCell"))) {
			System.err.println("Place cell type not implemented");
			System.exit(1);
		}

		cells = new LinkedList<PlaceCell>();
		Random random = RandomSingleton.getInstance();
		int i = 0;
		do {
			Coordinate prefLocation;
			prefLocation = createrPreferredLocation(nearGoalProb, goals, xmin, xmax, ymin, ymax, random);

			if (placeCellType.equals("ProportionalPlaceCell")) {
				cells.add(new ProportionalPlaceCell(prefLocation, placeRadius));
			} else if (placeCellType.equals("ExponentialPlaceCell")) {
				cells.add(new ExponentialPlaceCell(prefLocation, placeRadius));
			} else if (placeCellType.equals("WallExponentialPlaceCell")) {
				cells.add(new WallExponentialPlaceCell(prefLocation, placeRadius, random));
			}
			i++;
		} while (i < numCells);

		activationPort = new Float1dSparsePortMap(this, cells.size(), 4000);

		addOutPort("activation", activationPort);

		this.lRobot = (LocalizableRobot) robot;
		this.wRobot = (WallRobot) robot;
	}

	private Coordinate createrPreferredLocation(float nearGoalProb, List<Feeder> goals, float xmin, float xmax, float ymin,
			float ymax, Random random) {
		float x, y;
		if (random.nextFloat() < nearGoalProb) {
			int fIndex = random.nextInt(goals.size());
			Coordinate p = goals.get(fIndex).getPosition();
			x = (float) (p.x + random.nextFloat() * .2 - .1);
			y = (float) (p.y + random.nextFloat() * .2 - .1);
		} else {
			x = random.nextFloat() * (xmax - xmin) + xmin;
			y = random.nextFloat() * (ymax - ymin) + ymin;
		}
		return new Coordinate(x, y);
	}

	public void run() {
		int i = 0;
		float total = 0;
		Map<Integer, Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		for (PlaceCell pCell : cells) {
			float val = pCell.getActivation(lRobot.getPosition(), wRobot.getDistanceToClosestWall());
			if (val != 0)
				nonZero.put(i, val);
			total += val;
			i++;
		}

		if (Float.isNaN(total))
			System.out.println("Numeric error");
	}

	public List<PlaceCell> getCells() {
		return cells;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dSparsePortMap) getOutPort("activation")).clear();
	}

}
