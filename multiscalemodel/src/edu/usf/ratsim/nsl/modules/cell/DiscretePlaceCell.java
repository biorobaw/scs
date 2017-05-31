package edu.usf.ratsim.nsl.modules.cell;

import edu.usf.experiment.robot.GlobalWallRobot;

public interface DiscretePlaceCell {
	
	public float getActivation(int x, int y, GlobalWallRobot gwr);

}
