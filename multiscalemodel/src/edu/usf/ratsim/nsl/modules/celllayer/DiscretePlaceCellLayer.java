package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.ratsim.nsl.modules.cell.DiscretePlaceCell;
import edu.usf.ratsim.nsl.modules.cell.LargeDiscretePlaceCell;
import edu.usf.ratsim.nsl.modules.cell.SmallDiscretePlaceCell;

public class DiscretePlaceCellLayer extends Module implements PlaceCellLayer {

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
	private int width;
	private int height;
	private Object robot;
	private int maxActivation;

	public DiscretePlaceCellLayer(String name, int width, int height, boolean large, GlobalWallRobot gwr) {
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
				if (large)
					cells.add(new LargeDiscretePlaceCell(x, y));
				else 
					cells.add(new SmallDiscretePlaceCell(x, y));
			}
		
		// Initialize the port
		activationPort = new Float1dSparsePortMap(this, cells.size(), (large ? 6 : 1) / cells.size());
		addOutPort("output", activationPort);
		
		this.width = width;
		this.height = height;
		this.robot = gwr;
		
		this.maxActivation = large ? 8 : 1;
	}

	@Override
	public void run() {
		Point3fPort position = (Point3fPort) getInPort("position");
		activationPort.clear();

		Map<Integer, Float> activeCells = getActive(position.get());
		for (Integer cellIndex : activeCells.keySet()) {
			activationPort.set(cellIndex, activeCells.get(cellIndex));
		}
	}

	public Map<Integer, Float> getActive(Point3f position) {
		DiscreteCoord coord = new DiscreteCoord((int) position.x, (int) position.y);
		return stateActivationPerGridCell.get(coord);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public List<DiscretePlaceCell> getCells() {
		return cells;
	}

	@Override
	public void newTrial() {
		super.newTrial();
		
		stateActivationPerGridCell = new HashMap<DiscreteCoord, Map<Integer, Float>>();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				stateActivationPerGridCell.put(new DiscreteCoord(x, y), new HashMap<Integer, Float>());
			}
		
		// Upon a new trial, recompute place cell activations with the new wall setup
		// Assumes walls don't change throughout trial
		int i = 0;
		for (DiscretePlaceCell c : cells){
			for (int x = 0; x < width; x++)
				for (int y = 0; y < height; y++){
					float activation = c.getActivation(x, y, (GlobalWallRobot)robot);
					if (activation > 0) {
						stateActivationPerGridCell.get(new DiscreteCoord(x, y)).put(i, activation);
					}
			}
			i++;
		}
	}

	@Override
	public Float1dPort getActivationPort() {
		return activationPort;
	}

	@Override
	public float getMaxActivation() {
		return maxActivation;
	}
	
	

}
