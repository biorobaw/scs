package edu.usf.ratsim.nsl.modules.celllayer;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.ratsim.nsl.modules.cell.DiscretePlaceCell;

public class DiscretePlaceCellLayer extends Module {

	private List<DiscretePlaceCell> cells;
	private Float1dSparsePortMap activationPort;

	public DiscretePlaceCellLayer(String name, int width, int height, String string) {
		super(name);

		cells = new LinkedList<DiscretePlaceCell>();

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				cells.add(new DiscretePlaceCell(x, y));

		activationPort = new Float1dSparsePortMap(this, cells.size(), 1/cells.size());
		addOutPort("output", activationPort);
	}

	@Override
	public void run() {
		Point3fPort position = (Point3fPort) getInPort("position");
		
		int i = 0;
		for (DiscretePlaceCell c : cells) {
			float activation = c.getActivation((int) position.get().x, (int) position.get().y);
			if (activation != 0)
				activationPort.set(i, activation);
			i += 1;
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
