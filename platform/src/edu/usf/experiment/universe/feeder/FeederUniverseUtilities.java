package edu.usf.experiment.universe.feeder;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.Feeder;

public class FeederUniverseUtilities {

	// Filter Methods
	public static List<Feeder> getFlashingFeeders(List<Feeder> feeders) {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Feeder f : feeders)
			if (f.isFlashing())
				res.add(f);

		return res;
	}

	public static List<Feeder> getActiveFeeders(List<Feeder> feeders) {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Feeder f : feeders)
			if (f.isActive())
				res.add(f);

		return res;
	}
	
	public static List<Feeder> getEnabledFeeders(List<Feeder> feeders) {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Feeder f : feeders)
			if (f.isEnabled())
				res.add(f);
		return res;
	}
	
	public static List<Integer> getFeederNums(List<Feeder> feeders) {
		List<Integer> res = new LinkedList<Integer>();
		for (Feeder f : feeders)
			res.add(f.getId());
		return res;
	}
	
	// Involving position and food
	public static boolean hasRobotFoundFood(List<Feeder> feeders, Point3f robotPos, float close_thrs) {
		for (Feeder f : feeders) {
			if (f.isActive() && f.hasFood() && robotPos.distance(f.getPosition()) < close_thrs)
				return true;
		}

		return false;
	}
	

	public static boolean isRobotCloseToAFeeder(List<Feeder> feeders, Point3f robotPos, float close_thrs) {
		for (Feeder f : feeders)
			if (robotPos.distance(f.getPosition()) < close_thrs)
				return true;
		return false;
	}

	public static int getFeedingFeeder(List<Feeder> feeders, Point3f robotPos, float close_thrs) {
		for (Feeder f : feeders) {
			if (f.isActive())
				if (robotPos.distance(f.getPosition()) < close_thrs)
					return f.getId();
		}

		return -1;
	}


	public static boolean isRobotCloseToFeeder(Feeder f, Point3f robotPos, float close_thrs) {
		return robotPos.distance(f.getPosition()) < close_thrs;
	}


	public static float getDistanceToFeeder(Feeder f, Point3f robotPos) {
		return robotPos.distance(f.getPosition());
	}
	
	public static int getFoundFeeder(List<Feeder> feeders, Point3f robotPos, float close_thrs) {
		for (Feeder f : feeders)
			if (robotPos.distance(f.getPosition()) < close_thrs)
				return f.getId();

		return -1;
	}
	
	// Feeders and walls
	public static float wallDistanceToFeeders(List<Feeder> feeders, LineSegment wall) {
		float minDist = Float.MAX_VALUE;
		for (Feeder fn : feeders) {
			Point3f pos = fn.getPosition();
			Coordinate c = new Coordinate(pos.x, pos.y);
			if (wall.distance(c) < minDist)
				minDist = (float) wall.distance(c);
		}
		return minDist;
	}

	public static float shortestDistanceToFeeders(List<Feeder> feeders, Point2f x) {
		float minDist = Float.MAX_VALUE;
		Coordinate p = new Coordinate(x.x, x.y);
		for (Feeder fn : feeders) {
			Point3f pos = fn.getPosition();
			Coordinate c = new Coordinate(pos.x, pos.y);
			if (p.distance(c) < minDist)
				minDist = (float) p.distance(c);
		}
		return minDist;
	}
}
