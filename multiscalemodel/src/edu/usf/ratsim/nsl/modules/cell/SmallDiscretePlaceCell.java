package edu.usf.ratsim.nsl.modules.cell;

import edu.usf.experiment.robot.GlobalWallRobot;

public class SmallDiscretePlaceCell implements DiscretePlaceCell {

	private int x;
	private int y;

	public SmallDiscretePlaceCell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public float getActivation(int x, int y, GlobalWallRobot gwr) {
		if (this.x == x && this.y == y)
			return 1;
		else 
			return 0;
			
	}

}
