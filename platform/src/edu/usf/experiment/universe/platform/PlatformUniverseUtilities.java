package edu.usf.experiment.universe.platform;

import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.Platform;

public class PlatformUniverseUtilities {

	public static boolean hasRobotFoundPlatform(List<Platform> platforms, Point3f robotPos) {
		for (Platform plat : platforms)
			if (plat.getPosition().distance(robotPos) < plat.getRadius())
				return true;
		return false;
	}

	public static float shortestDistanceToPlatforms(List<Platform> platforms, LineSegment wall) {
		float minDist = Float.MAX_VALUE;
		for (Platform p : platforms) {
			Point3f pos = p.getPosition();
			Coordinate c = new Coordinate(pos.x, pos.y);
			if (wall.distance(c) < minDist)
				minDist = (float) wall.distance(c);
		}
		return minDist;
	}
}
