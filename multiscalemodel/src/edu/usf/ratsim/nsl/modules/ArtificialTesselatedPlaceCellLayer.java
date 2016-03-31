package edu.usf.ratsim.nsl.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Float1dSparsePortMap;
import edu.usf.ratsim.micronsl.Module;

public class ArtificialTesselatedPlaceCellLayer extends Module {

	private LinkedList<ArtificialPlaceCell> cells;

	private boolean active;

	private LocalizableRobot robot;

	private Float1dSparsePortMap activationPort;

	public ArtificialTesselatedPlaceCellLayer(String name, LocalizableRobot robot,
			float radius, int numCellsPerSide, String placeCellType,
			float xmin, float ymin, float xmax, float ymax) {
		super(name);

		active = true;

		cells = new LinkedList<ArtificialPlaceCell>();
		
		for (int i = 0; i < numCellsPerSide; i++){
			float x = xmin + i * (xmax - xmin) / (numCellsPerSide-1);
			for (int j = 0; j < numCellsPerSide; j++){
				float y = ymin + j * (ymax - ymin) / (numCellsPerSide-1);
				// Find if it intersects any wall
				if (placeCellType.equals("proportional"))
					cells.add(new ProportionalArtificialPlaceCell(new Point3f(
							x, y, 0)));
				else if (placeCellType.equals("exponential"))
					cells.add(new ExponentialArtificialPlaceCell(new Point3f(x,
							y, 0), radius));
				else
					throw new RuntimeException(
							"Place cell type not implemented");
			}
		}
			
		activationPort = new Float1dSparsePortMap(this, cells.size(), 4000);
		addOutPort("activation", activationPort);

		this.robot = robot;
	}

	public void run() {
		simRun(robot.getPosition(), robot.isFeederClose());
	}

	public float[] getActivationValues(Point3f pos) {
		float distanceToClosestWall = robot.getDistanceToClosestWall();
		float[] res = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			res[i] = cells.get(i).getActivation(pos, distanceToClosestWall);
		}

		return res;
	}

	public int getSize() {
		return cells.size();
	}

	public List<ArtificialPlaceCell> getCells() {
		return cells;
	}

	public void deactivate() {
		active = false;
	}

	public void simRun(Point3f pos, boolean isFeederClose) {
		simRun(pos, isFeederClose, robot.getDistanceToClosestWall());
	}

	public void simRun(Point3f pos, boolean isFeederClose,
			float distanceToClosestWall) {
		Map<Integer,Float> nonZero = activationPort.getNonZero();
		nonZero.clear();
		if (active) {
			int i = 0;
			float total = 0;
			
			
			for (ArtificialPlaceCell pCell : cells) {
				float val = pCell.getActivation(pos, distanceToClosestWall);
//				if (val < 0 || val > 1)
//					System.err
//							.println("Activation less than 0 or greater than 1: "
//									+ val);
				if (val != 0){
					nonZero.put(i, val);
					total += val;
				}
				i++;
			}
			
			if (Float.isNaN(total))
				System.out.println("Numeric error");
			
//			if (total != 0)
//				for (i = 0; i < activation.length; i++)
//					activation[i] = activation[i] / total * layerEnergy ;
		} else {
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dPortArray)getOutPort("activation")).set(0);
	}

}
