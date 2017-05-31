package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.ratsim.nsl.modules.cell.DiscretePlaceCell;
import edu.usf.ratsim.nsl.modules.cell.LargeDiscretePlaceCell;
import edu.usf.ratsim.nsl.modules.cell.SmallDiscretePlaceCell;

public class DiscretePlaceCellLayer extends Module {

	/**
	 * The list of place cells
	 */
	private List<DiscretePlaceCell> cells;
	/**
	 * The port with the activation values
	 */
	private Float1dSparsePortMap activationPort;
	/**
	 * The place cells that get active in each gridcell of the environment, with
	 * their cached activation
	 */
	private HashMap<DiscreteCoord, Map<Integer, Float>> stateActivationPerGridCell;

	public DiscretePlaceCellLayer(String name, int width, int height, boolean multiScale, GlobalWallRobot gwr) {
		super(name);

		cells = new ArrayList<DiscretePlaceCell>();

		// Initialize the map with an empty list in each bin
		stateActivationPerGridCell = new HashMap<DiscreteCoord, Map<Integer, Float>>();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				stateActivationPerGridCell.put(new DiscreteCoord(x, y), new HashMap<Integer, Float>());
			}

		// Add cells and precompute activation
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				addSmallCell(new SmallDiscretePlaceCell(x, y), x, y, gwr);
				if (multiScale)
					addLargeCell(new LargeDiscretePlaceCell(x, y), width, height, gwr);
			}
		
		// Normalize cell activation
		for (DiscreteCoord coord : stateActivationPerGridCell.keySet()){
			float total = 0;
			Map<Integer, Float> activeCells = stateActivationPerGridCell.get(coord);
			for (Integer cellIndex : activeCells.keySet()){
				total += activeCells.get(cellIndex);
			}
			for (Integer cellIndex : activeCells.keySet()){
				activeCells.put(cellIndex, activeCells.get(cellIndex) / total);
			}
		}

		// Initialize the port
		activationPort = new Float1dSparsePortMap(this, cells.size() * 2, 6 / cells.size());
		addOutPort("output", activationPort);
	}

	/**
	 * Adds the small cell to the cell list and to the corresponding bin in the
	 * map
	 * 
	 * @param c
	 *            the cell
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	private void addSmallCell(SmallDiscretePlaceCell c, int x, int y, GlobalWallRobot gwr) {
		cells.add(c);
		float activation = c.getActivation(x, y, gwr);
		stateActivationPerGridCell.get(new DiscreteCoord(x, y)).put(cells.size() - 1, activation);
	}

	/**
	 * Adds the large cell to the list and to all bins of the map in which the
	 * cell is active
	 * 
	 * @param c
	 *            the cell
	 * @param width
	 *            the width of the grid cell
	 * @param height
	 *            the height of the grid cell
	 * @param gwr
	 */
	private void addLargeCell(LargeDiscretePlaceCell c, int width, int height, GlobalWallRobot gwr) {
		cells.add(c);
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++){
				float activation = c.getActivation(x, y, gwr);
				if (activation > 0) {
					stateActivationPerGridCell.get(new DiscreteCoord(x, y)).put(cells.size() - 1, activation);
				}
			}
	}

	@Override
	public void run() {
		Point3fPort position = (Point3fPort) getInPort("position");
		activationPort.clear();

		getActive(activationPort, position.get());
	}

	public void getActive(Float1dSparsePort aPort, Point3f position) {
		DiscreteCoord coord = new DiscreteCoord((int) position.x, (int) position.y);
		Map<Integer, Float> activeCells = stateActivationPerGridCell.get(coord);
		for (Integer cellIndex : activeCells.keySet()) {
			aPort.set(cellIndex, activeCells.get(cellIndex));
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public List<DiscretePlaceCell> getCells() {
		return cells;
	}

}
