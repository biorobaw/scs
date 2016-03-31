package edu.usf.ratsim.nsl.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

public class ArtificialHDCellLayer extends Module {

	public float activation[];

	private List<ArtificialHDCell> cells;

	private LocalizableRobot robot;

	public ArtificialHDCellLayer(String name, int numCells, float radius,
			LocalizableRobot robot) {
		super(name);
		
		Random r = RandomSingleton.getInstance();
		cells = new LinkedList<ArtificialHDCell>();
		for (int i = 0; i < numCells; i++) {
			// Add a cell with center x,y
			cells.add(new ArtificialHDCell((float) (r.nextFloat() * 2 * Math.PI), radius));
		}

		activation = new float[cells.size()];
		addOutPort("activation", new Float1dPortArray(this, activation));

		this.robot = robot;
	}

	public void run() {
		float angle = robot.getOrientationAngle();
		simRun(angle);
	}

	public int getSize() {
		return activation.length;
	}

	public void simRun(float theta) {
		int i = 0;

		for (ArtificialHDCell pCell : cells) {
			activation[i] = pCell.getActivation(theta);
			// System.out.print(pCell.getActivation(angle) + " ");
			i++;
		}
		// System.out.println();
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void clear() {
		((Float1dPortArray)getOutPort("activation")).clear();
	}

}
