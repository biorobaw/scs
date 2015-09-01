package edu.usf.experiment.task;

import java.util.Random;

import javax.vecmath.Point2f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

public class AddRandomWallsTask extends Task {

	private static final float MAX_LENGHT_BEFORE_BREAK = 0.2f;
	private final float RADIUS = .5f;
	private static final float MIN_DIST_BETWEEN_WALLS = .001f;
	private static final float MIN_DIST_TO_FEEDERS = 0.05f;
	private static final float MAX_DIST_TO_PREV_WALLS = 0.05f;

	private int numWalls;
	private float length;

	public AddRandomWallsTask(ElementWrapper params) {
		super(params);

		numWalls = params.getChildInt("numWalls");
		length = params.getChildFloat("wallLength");
	}

	@Override
	public void perform(Experiment experiment) {
		perform(experiment.getUniverse());
	}

	@Override
	public void perform(Trial trial) {
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		perform(episode.getUniverse());
	}

	private void perform(Universe univ) {
		Random random = RandomSingleton.getInstance();

		for (int i = 0; i < numWalls; i++) {
			Point2f x1, x2, x3;
			if (length > MAX_LENGHT_BEFORE_BREAK) {
				LineSegment wall, wall2;

				do {
					// Get a point inside the desired area and close to a previous wall
					x1 = new Point2f();
					do {
						x1.x = random.nextFloat() * 2 * (RADIUS)
								- (RADIUS);
						x1.y = random.nextFloat() * 2 * (RADIUS)
								- (RADIUS);
					} while (x1.distance(new Point2f()) > RADIUS || univ.shortestDistanceToWalls(x1) > MAX_DIST_TO_PREV_WALLS);
					
					Point2f translation;
					float orientation;
					do {
						orientation = (float) (random.nextFloat() * Math.PI);
	
						translation = new Point2f();
						translation.x = (float) (length / 2 * Math.cos(orientation));
						translation.y = (float) (length / 2 * Math.sin(orientation));
	
						x2 = new Point2f(x1);
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
						translation.x = (float) (length / 2 * Math.cos(orientation
								+ breakAngle));
						translation.y = (float) (length / 2 * Math.sin(orientation
								+ breakAngle));
						x3.add(translation);
						
					} while (x3.distance(new Point2f()) > RADIUS);

					wall = new LineSegment(new Coordinate(x1.x, x1.y),
							new Coordinate(x2.x, x2.y));
					wall2 = new LineSegment(new Coordinate(x2.x, x2.y),
							new Coordinate(x3.x, x3.y));

				} while (univ.shortestDistanceToWalls(wall) < MIN_DIST_BETWEEN_WALLS
						|| univ.shortestDistanceToWalls(wall2) < MIN_DIST_BETWEEN_WALLS
						|| univ.wallDistanceToFeeders(wall) < MIN_DIST_TO_FEEDERS
						|| univ.wallDistanceToFeeders(wall2) < MIN_DIST_TO_FEEDERS);

				univ.addWall(x1.x, x1.y, x2.x, x2.y);

				univ.addWall(x2.x, x2.y, x3.x, x3.y);
			} else {

				LineSegment wall = null;
				do {
					// Create the first point random
					x1 = new Point2f();
					x1.x = random.nextFloat() * 2 * (RADIUS)
							- (RADIUS);
					x1.y = random.nextFloat() * 2 * (RADIUS)
							- (RADIUS);
					// Deside on orientation
					float orientation = (float) (random.nextFloat() * Math.PI);
					// Translation of x1 acording to orientation
					Point2f translation = new Point2f();
					// If the obstacle is too big, break it in two

					translation.x = (float) (length * Math.cos(orientation));
					translation.y = (float) (length * Math.sin(orientation));
					translation.add(x1);
					x2 = translation;
					wall = new LineSegment(new Coordinate(x1.x, x1.y),
							new Coordinate(x2.x, x2.y));

					// } while (univ.wallIntersectsOtherWalls(wall));
				} while (univ.shortestDistanceToWalls(wall) < MIN_DIST_BETWEEN_WALLS
						|| univ.wallDistanceToFeeders(wall) < MIN_DIST_TO_FEEDERS);

				univ.addWall(x1.x, x1.y, x2.x, x2.y);

			}
		}

	}

}
