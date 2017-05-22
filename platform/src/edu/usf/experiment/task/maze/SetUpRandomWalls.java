package edu.usf.experiment.task.maze;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.PlatformUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

public class SetUpRandomWalls extends Task {
	
	private final float X_RADIUS = 2f;
	private final float Y_RADIUS = .75f;
	private int watchDogCount;
	private static final float LENGTH = .75f;
	private static final int NUM_WALLS = 10;
	private static final float MIN_DIST_TO_PLATFORM_INTERIOR = 0.3f;
	private static final int MAX_WATCH_DOG = 10000;
	private static final float MIN_DIST_TO_ROBOT = .5f;
	private static final double MIN_DIST_TO_OTHER_WALLS = .4f;


	public SetUpRandomWalls(ElementWrapper params) {
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
		
		wu.revertWalls();
		pu.clearPlatforms();
		
		pu.addPlatform(new Point3f(-2f, 0, 0), .1f);
		
		float len = 2.5f;
		wu.addWall(len, len/2, -len, len/2);
		wu.addWall(-len, len/2, -len, -len/2);
		wu.addWall(-len, -len/2, len, -len/2);
		wu.addWall(len, -len/2, len, len/2);
		System.out.println("[+] Adding wmall walls");
		wu.setRevertWallPoint();
		
		while (!placeWalls(wu, pu, s))
			;
		System.out.println("[+] Small walls added");
	}
	
	public boolean placeWalls(WallUniverse wu, PlatformUniverse pu, Subject sub) {
		Random random = RandomSingleton.getInstance();
		watchDogCount = 0;

		// Add Outer Walls
		int j = 0;
		List<Float> angles = new LinkedList<Float>();
		while (j < NUM_WALLS) {
			LineSegment wall;

			do {
				float x = random.nextFloat() * 2 * X_RADIUS - X_RADIUS;
				float y = random.nextFloat() * 2 * Y_RADIUS - Y_RADIUS;
				float angle = (float) (random.nextFloat() * 2 * Math.PI);
				wall = getInnerWall(x, y, angle);
			} while (!watchDog() && !suitableWall(wall, wu, pu));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				wu.revertWalls();
				return false;
			}

			wu.addWall(wall);
			j++;
		}

		return true;
	}

	private boolean watchDog() {
		watchDogCount++;
		return watchDogCount > MAX_WATCH_DOG;
	}


	private boolean suitableWall(LineSegment wall, WallUniverse wu, PlatformUniverse pu) {
		return wall.p0.x < X_RADIUS && wall.p0.y < Y_RADIUS
				&& wall.p1.x < X_RADIUS && wall.p1.y < Y_RADIUS
				&& wall.distance(new Coordinate(0, 0)) > 0.05 
				&& wu.shortestDistanceToWalls(wall) > MIN_DIST_TO_OTHER_WALLS
				&& pu.shortestDistanceToPlatforms(wall) > MIN_DIST_TO_PLATFORM_INTERIOR
				&& wu.shortestDistanceToRobot(wall) > MIN_DIST_TO_ROBOT;
	}

	private LineSegment getInnerWall(double x, double y, double angle) {
		double x2, y2;
		x2 = x + LENGTH * Math.cos(angle);
		y2 = y + LENGTH * Math.sin(angle);

		LineSegment wall = new LineSegment(new Coordinate(x, y),
				new Coordinate(x2, y2));
		return wall;
	}

}
