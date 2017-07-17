package edu.usf.experiment.universe.wall;

import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class WallUniverseUtilities {

	// Wall and other walls
	public static boolean segmentIntersectsWalls(Set<Wall> walls, LineSegment wall) {
		boolean intersects = false;
		for (Wall w : walls)
			intersects = intersects || w.intersects(wall);

		return intersects;
	}

	// Walls and Robot
	public static float shortestDistanceToWalls(Set<Wall> walls, LineSegment wall) {
		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : walls)
			if (w.distanceTo(wall) < shortestDistance)
				shortestDistance = w.distanceTo(wall);

		return shortestDistance;
	}

	public static float shortestDistanceToWalls(Set<Wall> walls, Coordinate x1) {
		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : walls)
			if (w.distanceTo(x1) < shortestDistance)
				shortestDistance = w.distanceTo(x1);

		return shortestDistance;
	}

	public static float getDistanceToClosestWall(Set<Wall> walls, Coordinate p) {
		Coordinate p2 = new Coordinate(p.x, p.y);

		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : walls)
			if (w.distanceTo(p2) < shortestDistance)
				shortestDistance = w.distanceTo(p2);

		return shortestDistance;
	}

	public static float shortestDistanceToRobot(LineSegment wall, Coordinate robotPos) {
		return (float) wall.distance(new Coordinate(robotPos.x, robotPos.y));
	}

	/**
	 * Gives distance to nearest intersecting wall with the path (current pos,
	 * pos + Vector(rayX,raY))
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static double distanceToNearestWall(Set<Wall> walls, Coordinate p, float dx, float dy, float maxDistance) {
		Coordinate initCoordinate = new Coordinate(p.x, p.y);
		Coordinate finalCoordinate = new Coordinate(p.x + dx, p.y + dy);

		double minDistance = maxDistance;
		LineSegment path = new LineSegment(initCoordinate, finalCoordinate);
		Coordinate inter;
		double distance;
		for (Wall wall : walls) {

			if ((inter = path.intersection(wall.s)) != null
					&& (distance = inter.distance(initCoordinate)) < minDistance)
				minDistance = distance;
		}

		return minDistance;
	}

	public static float distanceToNearestWall(Set<Wall> walls, Coordinate robotPos, float angle, float distance) {
		return (float) distanceToNearestWall(walls, robotPos, (float)Math.cos(angle)*distance, (float)Math.sin(angle)*distance, distance);
	}
}
