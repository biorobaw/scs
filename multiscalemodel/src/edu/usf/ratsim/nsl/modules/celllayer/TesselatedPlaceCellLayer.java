package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.ratsim.nsl.modules.cell.ExponentialPlaceCell;
import edu.usf.ratsim.nsl.modules.cell.PlaceCell;
import edu.usf.ratsim.nsl.modules.cell.ProportionalPlaceCell;

/**
 * This layer locates place cells in a tesselated grid laid over the
 * environment.
 * 
 * @author Martin Llofriu
 *
 */
public class TesselatedPlaceCellLayer extends Module {

	/**
	 * The list of all place cells
	 */
	private LinkedList<PlaceCell> cells;

	/**
	 * Whether the layer is active or not
	 */
	private boolean active;

	/**
	 * A robot to provide localization information
	 */
	private LocalizableRobot lRobot;

	/**
	 * The activation output port. A sparse port is used for efficiency.
	 */
	private Float1dSparsePortMap activationPort;

	private WallRobot wRobot;

	/**
	 * Creates all the place cells and locates them in a tesselated grid laid
	 * over the environment.
	 * 
	 * @param name
	 *            The module's name
	 * @param robot
	 *            A robot capable of providing localization information
	 * @param radius
	 *            The radius of the place cells
	 * @param numCellsPerSide
	 *            Number of cells per side of the bounding box
	 * @param placeCellType
	 *            The type of place cells to use. Proportional and exponential
	 *            place cells are supported.
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
	 */
	public TesselatedPlaceCellLayer(String name, Robot robot, float radius, int numCellsPerSide,
			String placeCellType, float xmin, float ymin, float xmax, float ymax) {
		super(name);

		active = true;

		cells = new LinkedList<PlaceCell>();

		for (int i = 0; i < numCellsPerSide; i++) {
			float x = xmin + i * (xmax - xmin) / (numCellsPerSide - 1);
			for (int j = 0; j < numCellsPerSide; j++) {
				float y = ymin + j * (ymax - ymin) / (numCellsPerSide - 1);
				// Find if it intersects any wall
				if (placeCellType.equals("proportional"))
					cells.add(new ProportionalPlaceCell(new Coordinate(x, y), radius));
				else if (placeCellType.equals("exponential"))
					cells.add(new ExponentialPlaceCell(new Coordinate(x, y), radius));
				else
					throw new RuntimeException("Place cell type not implemented");
			}
		}

		activationPort = new Float1dSparsePortMap(this, cells.size(), 4000);
		addOutPort("activation", activationPort);

		this.lRobot = (LocalizableRobot) robot;
		this.wRobot = (WallRobot) robot;
	}
	
	/**
	 * Computes the current activation of all cells
	 */
	public void run() {
		run(lRobot.getPosition(), wRobot.getDistanceToClosestWall());
	}
	
	/**
	 * Computes the current activation of all cells given the current
	 * parameters.
	 * 
	 * @param pos
	 *            The current location of the animat
	 * @param distToWall
	 *            The distance to the closest wall
	 */
	public void run(Coordinate pos, float distanceToClosestWall) {
		Map<Integer, Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		if (active) {
			int i = 0;
			float total = 0;

			for (PlaceCell pCell : cells) {
				float val = pCell.getActivation(pos, distanceToClosestWall);
				if (val != 0) {
					nonZero.put(i, val);
					total += val;
				}
				i++;
			}

			if (Float.isNaN(total))
				System.out.println("Numeric error");
		}
	}
	
	/**
	 * Returns the activation of all cells
	 * @param pos The current position of the animat.
	 * @return An array of the activation values
	 */
	public float[] getActivationValues(Coordinate pos) {
		float distanceToClosestWall = wRobot.getDistanceToClosestWall();
		float[] res = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			res[i] = cells.get(i).getActivation(pos, distanceToClosestWall);
		}

		return res;
	}

	public List<PlaceCell> getCells() {
		return cells;
	}

	public void deactivate() {
		active = false;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dPortArray) getOutPort("activation")).clear();
	}

}
