package edu.usf.experiment.task.wall;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;
import edu.usf.micronsl.Model;

public class AddSmallWallsTask extends Task {

	private final float RADIUS = .5f;
	private int watchDogCount;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float LENGTH = .125f;
	private static final int NUM_WALLS = 16;
	private static final float NEAR_WALL_RADIUS = .44f;
	private static final float DISTANCE_INTERIOR_WALLS = .1f;
	private static final float MIN_DIST_TO_FEEDERS_INTERIOR = 0.1f;
	private static final double NUM_INTERIOR_WALLS = 6;
	private static final float DOUBLE_WALL_PROB = 0.2f;
	private static final double MIN_DIST_TO_OTHER_OUTER = .1;
	private static final int MAX_WATCH_DOG = 10000;
	private static final float MIN_ANGLE_DISTANCE_OUTER = (float) (2 * Math.PI / (2 * 8));
	private static final float MIN_DIST_TO_ROBOT = .1f;

	public AddSmallWallsTask(ElementWrapper params) {
		super(params);

	}

	
	@Override
	public void perform(Universe u, Subject s) {
		System.out.println("[+] Adding wmall walls");
		while (!putWalls(u, s))
			;
		System.out.println("[+] Small walls added");
	}

	public boolean putWalls(Universe univ, Subject sub) {
		if (!(univ instanceof WallUniverse))
			throw new IllegalArgumentException("");
		
		WallUniverse wu = (WallUniverse) univ;
		GlobalCameraUniverse gcu = (GlobalCameraUniverse) univ;
		
		Random random = RandomSingleton.getInstance();
		List<LineSegment> outerWalls = new LinkedList<LineSegment>();
		watchDogCount = 0;
		wu.setRevertWallPoint();

		// Add Outer Walls
		int j = 0;
		List<Float> angles = new LinkedList<Float>();
		while (j < NUM_WALLS - NUM_INTERIOR_WALLS) {
			boolean doubleWall = random.nextFloat() < DOUBLE_WALL_PROB;
			LineSegment wall;
			float angle;
			do {
				do {
					angle = (float) (random.nextDouble() * Math.PI * 2);
				} while (!watchDog()
						&& (!angles.isEmpty() && minDistance(angle, angles) < MIN_ANGLE_DISTANCE_OUTER));
				if (watchDog()) {
					System.out.println("Watch dog reached");
					wu.revertWalls();
					return false;
				}

				angles.add(angle);
				wall = getOuterWall(angle, doubleWall);

			} while (!watchDog() && !suitableOuterWall(wall, wu, gcu, outerWalls));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				wu.revertWalls();
				return false;
			}

			wu.addWall(wall);
			outerWalls.add(wall);
			if (doubleWall)
				j += 2;
			else
				j++;
		}

		while (j < NUM_WALLS) {
			LineSegment wall;

			do {
				float x = random.nextFloat() * 2 * RADIUS - RADIUS;
				float y = random.nextFloat() * 2 * RADIUS - RADIUS;
				float angle = (float) (random.nextFloat() * 2 * Math.PI);
				wall = getInnerWall(x, y, angle);
			} while (!watchDog() && !suitableInnerWall(wall, wu, gcu));

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

	private float minDistance(float angle, List<Float> angles) {
		float minDistance = Float.MAX_VALUE;
		for (Float angle2 : angles)
			if (Math.abs(GeomUtils.relativeAngle(angle, angle2)) < minDistance)
				minDistance = Math.abs(GeomUtils.relativeAngle(angle, angle2));
				
		return minDistance;
	}

	private boolean watchDog() {
		watchDogCount++;
		return watchDogCount > MAX_WATCH_DOG;
	}

	private boolean suitableOuterWall(LineSegment wall, WallUniverse univ, GlobalCameraUniverse gcu,
			List<LineSegment> outerWalls) {
		boolean feederUniverse = univ instanceof FeederUniverse;
		
		for (LineSegment w2 : outerWalls)
			if (w2.distance(wall) < MIN_DIST_TO_OTHER_OUTER)
				return false;
		return WallUniverseUtilities.shortestDistanceToWalls(univ.getWalls(), wall) > 0
				&& (!feederUniverse || FeederUniverseUtilities.wallDistanceToFeeders(((FeederUniverse)univ).getFeeders(),wall) > MIN_DIST_TO_FEEDERS)
				&& WallUniverseUtilities.shortestDistanceToRobot(wall, gcu.getRobotPosition()) > MIN_DIST_TO_ROBOT;
	}

	private boolean suitableInnerWall(LineSegment wall, WallUniverse univ,GlobalCameraUniverse gcu) {
		boolean feederUniverse = univ instanceof FeederUniverse;
		
		return wall.p0.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.p1.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.distance(new Coordinate(0, 0)) > 0.05 
				&& WallUniverseUtilities.shortestDistanceToWalls(univ.getWalls(),wall) > DISTANCE_INTERIOR_WALLS
				&& (!feederUniverse || FeederUniverseUtilities.wallDistanceToFeeders(((FeederUniverse)univ).getFeeders(),wall) > MIN_DIST_TO_FEEDERS_INTERIOR)
				&& WallUniverseUtilities.shortestDistanceToRobot(wall, gcu.getRobotPosition()) > MIN_DIST_TO_ROBOT;
	}

	private LineSegment getOuterWall(double angle, boolean doubleWall) {
		Coordinate outerPoint = new Coordinate();
		outerPoint.x = (float) (Math.cos(angle) * NEAR_WALL_RADIUS);
		outerPoint.y = (float) (Math.sin(angle) * NEAR_WALL_RADIUS);

		float length = LENGTH;
		if (doubleWall)
			length *= 2;

		Coordinate innerPoint = new Coordinate();
		innerPoint.x = (float) (Math.cos(angle) * (NEAR_WALL_RADIUS - length));
		innerPoint.y = (float) (Math.sin(angle) * (NEAR_WALL_RADIUS - length));

		LineSegment wall = new LineSegment(new Coordinate(outerPoint.x,
				outerPoint.y), new Coordinate(innerPoint.x, innerPoint.y));
		return wall;
	}

	private LineSegment getInnerWall(double x, double y, double angle) {
		double x2, y2;
		x2 = x + LENGTH * Math.cos(angle);
		y2 = y + LENGTH * Math.sin(angle);

		LineSegment wall = new LineSegment(new Coordinate(x, y),
				new Coordinate(x2, y2));
		return wall;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10000; i++) {
			ElementWrapper root = XMLExperimentParser
					.loadRoot("multiscalemodel/src/edu/usf/ratsim/experiment/xml/multiFeedersTrainRecallSmallObs.xml");
			Universe univ = Universe.load(root, ".");
			Robot robot = Robot.load(root,univ);
			Model model = Subject.load(root.getChild("model"), robot);
			AddSmallWallsTask t = new AddSmallWallsTask(null);
			while (!t.putWalls(univ, new Subject("sub", "group", model, robot)))
				;
			System.out.println("walls added");
			// try {
			// Thread.sleep(3000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}
		System.exit(0);
	}



}
