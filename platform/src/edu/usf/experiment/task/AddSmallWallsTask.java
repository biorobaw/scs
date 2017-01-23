package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.SubjectLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;

public class AddSmallWallsTask extends Task {

	private final float RADIUS = .5f;
	private int watchDogCount;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float LENGTH = .125f;
	private static final int NUM_WALLS = 14;
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
	public void perform(Experiment experiment) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(experiment.getUniverse(), (SubjectOld)experiment.getSubject()))
			;
		System.out.println("[+] Small walls added");
	}

	@Override
	public void perform(Trial trial) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(trial.getUniverse(), (SubjectOld)trial.getSubject()))
			;
		System.out.println("[+] Small walls added");
	}

	@Override
	public void perform(Episode episode) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(episode.getUniverse(), (SubjectOld)episode.getSubject()))
			;
		System.out.println("[+] Small walls added");
	}

	public boolean perform(Universe univ, SubjectOld sub) {
		Random random = RandomSingleton.getInstance();
		List<LineSegment> outerWalls = new LinkedList<LineSegment>();
		watchDogCount = 0;
		univ.setRevertWallPoint();

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
					univ.revertWalls();
					return false;
				}

				angles.add(angle);
				wall = getOuterWall(angle, doubleWall);

			} while (!watchDog() && !suitableOuterWall(wall, univ, outerWalls));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				univ.revertWalls();
				return false;
			}

			univ.addWall(wall);
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
			} while (!watchDog() && !suitableInnerWall(wall, univ));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				univ.revertWalls();
				return false;
			}

			univ.addWall(wall);
			j++;
		}

		return true;
	}

	private float minDistance(float angle, List<Float> angles) {
		float minDistance = Float.MAX_VALUE;
		for (Float angle2 : angles)
			if (Math.abs(GeomUtils.angleDiff(angle, angle2)) < minDistance)
				minDistance = Math.abs(GeomUtils.angleDiff(angle, angle2));
				
		return minDistance;
	}

	private boolean watchDog() {
		watchDogCount++;
		return watchDogCount > MAX_WATCH_DOG;
	}

	private boolean suitableOuterWall(LineSegment wall, Universe univ,
			List<LineSegment> outerWalls) {
		for (LineSegment w2 : outerWalls)
			if (w2.distance(wall) < MIN_DIST_TO_OTHER_OUTER)
				return false;
		return univ.shortestDistanceToWalls(wall) > 0
				&& univ.wallDistanceToFeeders(wall) > MIN_DIST_TO_FEEDERS
				&& univ.shortestDistanceToRobot(wall) > MIN_DIST_TO_ROBOT;
	}

	private boolean suitableInnerWall(LineSegment wall, Universe univ) {
		return wall.p0.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.p1.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.distance(new Coordinate(0, 0)) > 0.05 
				&& univ.shortestDistanceToWalls(wall) > DISTANCE_INTERIOR_WALLS
				&& univ.shortestDistanceToFeeders(wall) > MIN_DIST_TO_FEEDERS_INTERIOR
				&& univ.shortestDistanceToRobot(wall) > MIN_DIST_TO_ROBOT;
	}

	private LineSegment getOuterWall(double angle, boolean doubleWall) {
		Point2f outerPoint = new Point2f();
		outerPoint.x = (float) (Math.cos(angle) * NEAR_WALL_RADIUS);
		outerPoint.y = (float) (Math.sin(angle) * NEAR_WALL_RADIUS);

		float length = LENGTH;
		if (doubleWall)
			length *= 2;

		Point2f innerPoint = new Point2f();
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
			Universe univ = UniverseLoader.getInstance().load(root, ".");
			RobotOld robot = RobotLoader.getInstance().load(root);
			SubjectOld subject = (SubjectOld)SubjectLoader.getInstance().load("a", "a",
					root.getChild("model"), robot);
			AddSmallWallsTask t = new AddSmallWallsTask(null);
			while (!t.perform(univ, subject))
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
