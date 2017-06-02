package edu.usf.ratsim.nsl.modules.cell;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;

public class LargeDiscretePlaceCell implements DiscretePlaceCell {

	private int x;
	private int y;

	public LargeDiscretePlaceCell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public float getActivation(int x, int y, GlobalWallRobot gwr) {
		LineSegment path = new LineSegment(new Coordinate(this.x + .5, this.y + .5), new Coordinate(x + .5, y + .5));
		if (WallUniverseUtilities.segmentIntersectsWalls(gwr.getWalls(), path))
			return 0;
		
		
		int blockDist = blockDist(this.x, this.y, x, y);
		if (blockDist == 0)
			return 1;
		else if (blockDist == 1)
			return 0.75f;
		else if (blockDist == 2)
			return 0.5f;
		else
			return 0;

	}

	private int blockDist(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

}
