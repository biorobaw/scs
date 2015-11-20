package edu.usf.experiment.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.PreExperiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotLoader;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.SubjectLoader;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.UniverseLoader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.XMLExperimentParser;

public class AddLargeWallsTask extends Task {

	private final float RADIUS = .50f;
	private int watchDogCount;
	private Random random;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float LENGTH = .2f;
	private static final int NUM_WALLS = 8;
	private static final float NEAR_WALL_RADIUS = .44f;
	private static final float DISTANCE_INTERIOR_WALLS = .1f;
	private static final float MIN_DIST_TO_FEEDERS_INTERIOR = 0.1f;
	private static final double NUM_INTERIOR_WALLS = 2;
	private static final float DOUBLE_WALL_PROB = 1f;
	private static final double MIN_DIST_TO_OTHER_OUTER = .1;
	private static final int MAX_WATCH_DOG = 10000;
	private static final float MIN_ANGLE_DISTANCE_OUTER = (float) (2 * Math.PI / (2 * 8));
	private static final double BREAK_ANGLE = Math.PI / 8;

	public AddLargeWallsTask(ElementWrapper params) {
		super(params);

	}

	@Override
	public void perform(Experiment experiment) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(experiment.getUniverse(), experiment.getSubject()))
			;
		System.out.println("[+] Large walls added");
	}

	@Override
	public void perform(Trial trial) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(trial.getUniverse(), trial.getSubject()))
			;
		System.out.println("[+] Large walls added");
	}

	@Override
	public void perform(Episode episode) {
		System.out.println("[+] Adding wmall walls");
		while (!perform(episode.getUniverse(), episode.getSubject()))
			;
		System.out.println("[+] Large walls added");
	}

	private boolean perform(Universe univ, Subject sub) {
		random = RandomSingleton.getInstance();
		List<LineSegment> outerWalls = new LinkedList<LineSegment>();
		watchDogCount = 0;
		univ.setRevertWallPoint();

		// Add Outer Walls
		int j = 0;
		List<Float> angles = new LinkedList<Float>();
		while (j < NUM_WALLS - NUM_INTERIOR_WALLS) {
			boolean doubleWall = random.nextFloat() < DOUBLE_WALL_PROB;
			List<LineSegment> walls;
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
				walls = getOuterWall(angle, doubleWall);

			} while (!watchDog() && !suitableOuterWall(walls, univ, outerWalls));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				univ.revertWalls();
				return false;
			}

			for (LineSegment wall : walls){
				univ.addWall(wall);
				outerWalls.add(wall);
				
			}
			if (doubleWall)
				j += 2;
			else
				j++;
		}

		while (j < NUM_WALLS) {
			List<LineSegment> walls;

			do {
				float x = random.nextFloat() * 2 * RADIUS - RADIUS;
				float y = random.nextFloat() * 2 * RADIUS - RADIUS;
				float angle = (float) (random.nextFloat() * 2 * Math.PI);
				walls = getInnerWall(x, y, angle);
			} while (!watchDog() && !suitableInnerWall(walls, univ));

			if (watchDog()) {
				System.out.println("Watch dog reached");
				univ.revertWalls();
				return false;
			}

			for (LineSegment wall : walls)
				univ.addWall(wall);
			j+=2;
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

	private boolean suitableOuterWall(List<LineSegment> walls, Universe univ,
			List<LineSegment> outerWalls) {
		boolean suitable = true;
		for (LineSegment w2 : outerWalls) {
			for (LineSegment wall : walls) {
				if (w2.distance(wall) < MIN_DIST_TO_OTHER_OUTER)
					return false;
				suitable = suitable
						&& univ.shortestDistanceToWalls(wall) > 0
						&& univ.wallDistanceToFeeders(wall) > MIN_DIST_TO_FEEDERS;
			}
		}
		return suitable;
	}

	private boolean suitableInnerWall(List<LineSegment> walls, Universe univ) {
		boolean suitable = true;
		for (LineSegment wall : walls)
			suitable &= wall.p0.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.p1.distance(new Coordinate(0, 0)) < RADIUS
				&& wall.distance(new Coordinate(0, 0)) > 0.05
				&& univ.shortestDistanceToWalls(wall) > DISTANCE_INTERIOR_WALLS
				&& univ.shortestDistanceToFeeders(wall) > MIN_DIST_TO_FEEDERS_INTERIOR;
		return suitable;
	}

	private List<LineSegment> getOuterWall(double angle, boolean doubleWall) {
		List<LineSegment> walls = new LinkedList<LineSegment>();

		Point2f outerPoint = new Point2f();
		outerPoint.x = (float) (Math.cos(angle) * NEAR_WALL_RADIUS);
		outerPoint.y = (float) (Math.sin(angle) * NEAR_WALL_RADIUS);

		float length = LENGTH;
		Point2f middlePoint = new Point2f();
		middlePoint.x = (float) (Math.cos(angle) * (NEAR_WALL_RADIUS - length));
		middlePoint.y = (float) (Math.sin(angle) * (NEAR_WALL_RADIUS - length));

		LineSegment wall = new LineSegment(new Coordinate(outerPoint.x,
				outerPoint.y), new Coordinate(middlePoint.x, middlePoint.y));
		walls.add(wall);

		if (doubleWall) {
			angle += BREAK_ANGLE * Math.signum(random.nextDouble());
			Point2f innerPoint = new Point2f();
			innerPoint.x = (float) (middlePoint.x - Math.cos(angle) * length);
			innerPoint.y = (float) (middlePoint.y - Math.sin(angle) * length);

			LineSegment wall2 = new LineSegment(new Coordinate(middlePoint.x,
					middlePoint.y), new Coordinate(innerPoint.x, innerPoint.y));
			walls.add(wall2);
		}

		return walls;
	}

	private List<LineSegment> getInnerWall(double x, double y, double angle) {
		double x2, y2, x3, y3;
		x2 = x + LENGTH * Math.cos(angle);
		y2 = y + LENGTH * Math.sin(angle);
		angle += BREAK_ANGLE * Math.signum(random.nextDouble());
		x3 = x2 + LENGTH * Math.cos(angle);
		y3 = y2 + LENGTH * Math.sin(angle);
		
		LineSegment wall = new LineSegment(new Coordinate(x, y),
				new Coordinate(x2, y2));
		LineSegment wall2 = new LineSegment(new Coordinate(x2, y2),
				new Coordinate(x3, y3));
		List<LineSegment> walls = new LinkedList<LineSegment>();
		walls.add(wall);
		walls.add(wall2);
		return walls;
	}

	public static void main(String[] args) {
		// for (int i = 0; i < 10000; i++) {
		ElementWrapper root = XMLExperimentParser
				.loadRoot("multiscalemodel/src/edu/usf/ratsim/experiment/xml/multiFeedersTrainRecallLargeObs.xml");
		new PreExperiment(
				"multiscalemodel/src/edu/usf/ratsim/experiment/xml/multiFeedersTrainRecallLargeObs.xml",
				"logs/Experiment/");
		Universe univ = UniverseLoader.getInstance().load(root,
				"logs/Experiment/");
		Robot robot = RobotLoader.getInstance().load(root);
		Subject subject = SubjectLoader.getInstance().load("a", "a",
				root.getChild("model"), robot);
		AddLargeWallsTask t = new AddLargeWallsTask(null);
		while (!t.perform(univ, subject))
			;
		System.out.println("walls added");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// }
		System.exit(0);
	}
}
