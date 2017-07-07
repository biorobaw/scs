package edu.usf.experiment.task.wall;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.experiment.utils.RigidTransformation;

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

	public void perform(Universe u, Subject s) {
		if (!(u instanceof WallUniverse))
			throw new IllegalArgumentException("");

		WallUniverse wu = (WallUniverse) u;
		Random random = RandomSingleton.getInstance();

		boolean feederUniverse = u instanceof FeederUniverse;

		for (int i = 0; i < numWalls; i++) {
			Coordinate x1, x2, x3;
			if (length > MAX_LENGHT_BEFORE_BREAK) {
				LineSegment wall, wall2;

				do {
					// Get a point inside the desired area and close to a
					// previous wall
					x1 = new Coordinate();
					do {
						x1.x = random.nextFloat() * 2 * (RADIUS) - (RADIUS);
						x1.y = random.nextFloat() * 2 * (RADIUS) - (RADIUS);
					} while (x1.distance(new Coordinate()) > RADIUS || WallUniverseUtilities
							.shortestDistanceToWalls(wu.getWalls(), x1) > MAX_DIST_TO_PREV_WALLS);

					RigidTransformation translation;
					float orientation;
					do {

						orientation = (float) (random.nextFloat() * Math.PI);

						translation = new RigidTransformation((float) (length / 2 * Math.cos(orientation)),
								(float) (length / 2 * Math.sin(orientation)), 0f);
						x2 = new Coordinate();
						translation.transform(x1, x2);

					} while (x2.distance(new Coordinate()) > RADIUS);

					do {
						float breakAngle;
						if (random.nextFloat() > .5)
							breakAngle = (float) (Math.PI / 3);
						else
							breakAngle = (float) (Math.PI / 4);
						if (random.nextFloat() > .5)
							breakAngle = -breakAngle;

						translation = new RigidTransformation((float) (length / 2 * Math.cos(orientation + breakAngle)),
								(float) (length / 2 * Math.sin(orientation + breakAngle)), 0f);
						x3 = new Coordinate();
						translation.transform(x2, x3);

					} while (x3.distance(new Coordinate()) > RADIUS);

					wall = new LineSegment(new Coordinate(x1.x, x1.y), new Coordinate(x2.x, x2.y));
					wall2 = new LineSegment(new Coordinate(x2.x, x2.y), new Coordinate(x3.x, x3.y));

				} while (WallUniverseUtilities.shortestDistanceToWalls(wu.getWalls(), wall) < MIN_DIST_BETWEEN_WALLS
						|| WallUniverseUtilities.shortestDistanceToWalls(wu.getWalls(), wall2) < MIN_DIST_BETWEEN_WALLS
						|| (feederUniverse && FeederUniverseUtilities
								.wallDistanceToFeeders(((FeederUniverse) wu).getFeeders(), wall) < MIN_DIST_TO_FEEDERS)
						|| (feederUniverse
								&& FeederUniverseUtilities.wallDistanceToFeeders(((FeederUniverse) wu).getFeeders(),
										wall2) < MIN_DIST_TO_FEEDERS));

				wu.addWall((float) x1.x, (float) x1.y, (float) x2.x, (float) x2.y);

				wu.addWall((float) x2.x, (float) x2.y, (float) x3.x, (float) x3.y);
			} else {
				LineSegment wall = null;
				do {
					// Create the first point random
					x1 = new Coordinate();
					x1.x = random.nextFloat() * 2 * (RADIUS) - (RADIUS);
					x1.y = random.nextFloat() * 2 * (RADIUS) - (RADIUS);
					// Deside on orientation
					float orientation = (float) (random.nextFloat() * Math.PI);
					// Translation of x1 acording to orientation
					// If the obstacle is too big, break it in two

					RigidTransformation translation = new RigidTransformation((float) (length * Math.cos(orientation)),
							(float) (length * Math.sin(orientation)), 0f);
					x2 = new Coordinate();
					translation.transform(x1, x2);
					wall = new LineSegment(new Coordinate(x1.x, x1.y), new Coordinate(x2.x, x2.y));

					// } while (univ.wallIntersectsOtherWalls(wall));
				} while (WallUniverseUtilities.shortestDistanceToWalls(wu.getWalls(), wall) < MIN_DIST_BETWEEN_WALLS
						|| (feederUniverse
								&& FeederUniverseUtilities.wallDistanceToFeeders(((FeederUniverse) wu).getFeeders(),
										wall) < MIN_DIST_TO_FEEDERS));

				wu.addWall((float) x1.x, (float) x1.y, (float) x2.x, (float) x2.y);

			}
		}

	}

}
