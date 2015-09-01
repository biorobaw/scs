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
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

public class AddLargeWallsTask extends Task {

	private final float RADIUS = .4f;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float NEAR_WALL_RADIUS = .49f;
	private static final float LENGTH = .4f;
	private static final int NUM_WALLS = 5;
	private static final float RADIUS_THIRD_POINT = .30f;
	private static final float DISTANCE_INTERIOR_WALLS = .1f;
	private static final float MIN_DIST_TO_FEEDERS_INTERIOR = 0.1f;
	private static final double NUM_INTERIOR_WALLS = 4;

	public AddLargeWallsTask(ElementWrapper params) {
		super(params);

	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse(), experiment.getSubject());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse(), trial.getSubject());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse(), episode.getSubject());
	}

	private void perform(Universe univ, Subject sub) {
		Random random = RandomSingleton.getInstance();

		List<Point2f> nearWall;
		LineSegment wall, wall2;
		boolean noClosePoints;
		do {
			// Pick an orientation to seed the three walls near the outside
			float orientation = (float) (random.nextFloat() * Math.PI);
			nearWall = new LinkedList<Point2f>();
			noClosePoints = true;
			for (int j = 0; j < NUM_INTERIOR_WALLS; j++) {
				Point2f x = new Point2f();
				x.x = (float) Math.cos(orientation + j * 2 * Math.PI / NUM_INTERIOR_WALLS)
						* NEAR_WALL_RADIUS;
				x.y = (float) Math.sin(orientation + j * 2 * Math.PI / NUM_INTERIOR_WALLS)
						* NEAR_WALL_RADIUS;
				noClosePoints = noClosePoints
						&& univ.shortestDistanceToFeeders(x) > MIN_DIST_TO_FEEDERS;
				nearWall.add(x);
			}
		} while (!noClosePoints);

		// For each point, find a wall that is not too close to feeders and
		// not intersecting other walls
		for (Point2f seed : nearWall) {
			do {
				float orientation;
				Point2f translation, x2, x3;
				do {
					orientation = (float) (random.nextFloat() * 2 * Math.PI);

					translation = new Point2f();
					translation.x = (float) (LENGTH / 2 * Math.cos(orientation));
					translation.y = (float) (LENGTH / 2 * Math.sin(orientation));

					x2 = new Point2f(seed);
					x2.add(translation);
				} while (x2.distance(new Point2f()) > RADIUS);

				do {
					float breakAngle;
					if (random.nextFloat() > .5)
						breakAngle = (float) (Math.PI / 3);
					else
						breakAngle = (float) (Math.PI / 4);
					if (random.nextFloat() > .5)
						breakAngle = -breakAngle;

					x3 = new Point2f(x2);
					translation.x = (float) (LENGTH / 2 * Math.cos(orientation
							+ breakAngle));
					translation.y = (float) (LENGTH / 2 * Math.sin(orientation
							+ breakAngle));
					x3.add(translation);

				} while (x3.distance(new Point2f()) > RADIUS_THIRD_POINT); // The
																			// last
																			// point
																			// should
																			// not
																			// be
																			// too
																			// close
																			// to
																			// the
																			// walls

				wall = new LineSegment(new Coordinate(seed.x, seed.y),
						new Coordinate(x2.x, x2.y));
				wall2 = new LineSegment(new Coordinate(x2.x, x2.y),
						new Coordinate(x3.x, x3.y));

			} while (univ.shortestDistanceToWalls(wall) == 0
					|| univ.shortestDistanceToWalls(wall2) < DISTANCE_INTERIOR_WALLS
					|| univ.wallDistanceToFeeders(wall) < MIN_DIST_TO_FEEDERS
					|| univ.wallDistanceToFeeders(wall2) < MIN_DIST_TO_FEEDERS);

			univ.addWall(wall);

			univ.addWall(wall2);

		}

//		
		// Add interior walls
		for (int i = 0; i < NUM_WALLS - NUM_INTERIOR_WALLS; i++) {
			Point2f firstPoint;
			LineSegment wall1;
			float orientation;
			Point2f secondPoint;
			Point2f thirdPoint;
			do {
				float x = random.nextFloat() - .5f;
				float y = random.nextFloat() - .5f;
				firstPoint = new Point2f(x, y);

				orientation = (float) (random.nextFloat() * 2 * Math.PI);

				Point2f translation = new Point2f();
				translation.x = (float) (LENGTH / 2 * Math.cos(orientation));
				translation.y = (float) (LENGTH / 2 * Math.sin(orientation));
				secondPoint = new Point2f(firstPoint);
				secondPoint.add(translation);
				wall1 = new LineSegment(new Coordinate(firstPoint.x,
						firstPoint.y), new Coordinate(secondPoint.x,
						secondPoint.y));

				float breakAngle;
				if (random.nextFloat() > .5)
					breakAngle = (float) (Math.PI / 3);
				else
					breakAngle = (float) (Math.PI / 4);
				if (random.nextFloat() > .5)
					breakAngle = -breakAngle;

				thirdPoint = new Point2f(secondPoint);
				translation = new Point2f();
				translation.x = (float) (LENGTH / 2 * Math.cos(orientation
						+ breakAngle));
				translation.y = (float) (LENGTH / 2 * Math.sin(orientation
						+ breakAngle));
				thirdPoint.add(translation);

				wall2 = new LineSegment(new Coordinate(secondPoint.x,
						secondPoint.y), new Coordinate(thirdPoint.x,
						thirdPoint.y));

			} while (firstPoint.distance(new Point2f()) > RADIUS
					|| secondPoint.distance(new Point2f()) > RADIUS
					|| thirdPoint.distance(new Point2f()) > RADIUS
					|| univ.shortestDistanceToWalls(wall1) < DISTANCE_INTERIOR_WALLS
					|| univ.shortestDistanceToFeeders(secondPoint) < MIN_DIST_TO_FEEDERS_INTERIOR
					|| univ.shortestDistanceToWalls(wall2) < DISTANCE_INTERIOR_WALLS
					|| univ.shortestDistanceToFeeders(thirdPoint) <  MIN_DIST_TO_FEEDERS_INTERIOR);

			univ.addWall(wall1);
			univ.addWall(wall2);

		}
		
//		sub.restoreExploration();
	}
}
