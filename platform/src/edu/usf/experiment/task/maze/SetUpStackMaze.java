package edu.usf.experiment.task.maze;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.PlatformUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpStackMaze extends Task {

	private static final int NUM_WALLS = 8;
	private static final float INTER_WALL_SPACE = .5f;
	private static final float WALL_LENGHT = 2;
	private static final float HALF_HEIGHT = (NUM_WALLS + 1)/ 2f * INTER_WALL_SPACE;

	public SetUpStackMaze(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s) {
		if (!(u instanceof WallUniverse))
			throw new IllegalArgumentException("");
		
		WallUniverse wu = (WallUniverse) u;
		
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		
		wu.clearWalls();
		pu.clearPlatforms();

		// Add inner walls
		List<LineSegment> segments = new LinkedList<LineSegment>();
		float currY = -HALF_HEIGHT + INTER_WALL_SPACE;
		for (int i = 0; i < NUM_WALLS; i += 2) {
			segments.add(new LineSegment(new Coordinate(3 * WALL_LENGHT / 4, currY),
					new Coordinate(-WALL_LENGHT / 4, currY)));
			currY += INTER_WALL_SPACE;
			segments.add(new LineSegment(new Coordinate(-3 * WALL_LENGHT / 4, currY),
					new Coordinate(WALL_LENGHT / 4, currY)));
			currY += INTER_WALL_SPACE;
		}

		// Add side walls
		segments.add(new LineSegment(new Coordinate(-3 * WALL_LENGHT / 4, -HALF_HEIGHT),
				new Coordinate(-3 * WALL_LENGHT / 4, HALF_HEIGHT)));
		segments.add(new LineSegment(new Coordinate(3 * WALL_LENGHT / 4, -HALF_HEIGHT),
				new Coordinate(3 * WALL_LENGHT / 4, HALF_HEIGHT)));
		segments.add(new LineSegment(new Coordinate(-3 * WALL_LENGHT / 4, -HALF_HEIGHT),
				new Coordinate(3 * WALL_LENGHT / 4, -HALF_HEIGHT)));

		for (LineSegment seg : segments)
			wu.addWall(seg);

		pu.addPlatform(new Point3f(0f, HALF_HEIGHT + INTER_WALL_SPACE /2, 0), .05f);
	}

}
