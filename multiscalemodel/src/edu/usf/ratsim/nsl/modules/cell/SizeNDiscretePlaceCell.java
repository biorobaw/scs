package edu.usf.ratsim.nsl.modules.cell;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;

public class SizeNDiscretePlaceCell implements DiscretePlaceCell {

	private int x;
	private int y;
	private int size;
	private boolean wallInteraction;

	/**
	 * Create a custom size discrete place cell
	 * @param x the preferred x location
	 * @param y the preferred y location
	 * @param size the radius of the place field. 0 means a 1 square field, 1 means a on square field (4 neighbors - 5 cells)
	 * @param wallInteraction whether the place field is cut by the presence of a wall or not
	 */
	public SizeNDiscretePlaceCell(int x, int y, int size, boolean wallInteraction) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.wallInteraction = wallInteraction;
	}
	
	/**
	 * Create a custom size discrete place cell
	 * @param x the preferred x location
	 * @param y the preferred y location
	 * @param size the radius of the place field. 0 means a 1 square field, 1 means a on square field (4 neighbors - 5 cells)
	 */
	public SizeNDiscretePlaceCell(int x, int y, int size) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.wallInteraction = true;
	}

	public float getActivation(int x, int y, GlobalWallRobot gwr) {
		LineSegment path = new LineSegment(new Coordinate(this.x + .5, this.y + .5), new Coordinate(x + .5, y + .5));
		if (wallInteraction && WallUniverseUtilities.segmentIntersectsWalls(gwr.getWalls(), path))
			return 0;
		
		
		int blockDist = blockDist(this.x, this.y, x, y);
		
		if (blockDist == 0)
			return 1;
		else if (blockDist > size)
			return 0;
		else
			return 1 - blockDist / (2*size);
		
	}

	private int blockDist(int x1, int y1, int x2, int y2) {
		return (int) Math.floor(Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}

}
