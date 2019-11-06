package com.github.biorobaw.scs.experiment.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.github.biorobaw.scs.simulation.object.maze_elements.Feeder;

public class FeederUtils {

	// Filter Methods	
	public static List<Feeder> getFeedersWithFood(Collection<Feeder> feeders) {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Feeder f : feeders)
			if (f.hasFood)
				res.add(f);
		return res;
	}
	
	public static List<Integer> getFeederNums(List<Feeder> feeders) {
		List<Integer> res = new LinkedList<Integer>();
		for (Feeder f : feeders)
			res.add(f.feeder_id);
		return res;
	}
	
//	// Involving position and food
//	public static boolean hasRobotFoundFood(List<Feeder> feeders, Coordinate robotPos, float close_thrs) {
//		for (Feeder f : feeders) {
//			if (f.isActive() && f.hasFood() && robotPos.distance(f.getPosition()) < close_thrs){
////				System.out.println("Found food, distance: " + robotPos.distance(f.getPosition()));
////				System.out.println(robotPos.x+ " " + robotPos.y+ " " + f.getPosition().x + " " + f.getPosition().y +" " + close_thrs);
//				return true;
//			}
//		}
//
//		return false;
//	}
	

//	public static boolean isRobotCloseToAFeeder(List<Feeder> feeders, Coordinate robotPos, float close_thrs) {
//		for (Feeder f : feeders)
//			if (robotPos.distance(f.getPosition()) < close_thrs)
//				return true;
//		return false;
//	}

//	public static int getFeedingFeeder(List<Feeder> feeders, Coordinate robotPos, float close_thrs) {
//		for (Feeder f : feeders) {
//			if (f.isActive())
//				if (robotPos.distance(f.getPosition()) < close_thrs)
//					return f.getId();
//		}
//
//		return -1;
//	}


//	public static boolean isRobotCloseToFeeder(Feeder f, Coordinate robotPos, float close_thrs) {
//		return robotPos.distance(f.getPosition()) < close_thrs;
//	}


//	public static float getDistanceToFeeder(Feeder f, Coordinate robotPos) {
//		return (float) robotPos.distance(f.getPosition());
//	}
	
//	public static int getFoundFeeder(List<Feeder> feeders, Coordinate robotPos, float close_thrs) {
//		for (Feeder f : feeders)
//			if (robotPos.distance(f.getPosition()) < close_thrs)
//				return f.getId();
//
//		return -1;
//	}
	
	// Feeders and walls
//	public static float wallDistanceToFeeders(List<Feeder> feeders, LineSegment wall) {
//		float minDist = Float.MAX_VALUE;
//		for (Feeder fn : feeders) {
//			Coordinate pos = fn.getPosition();
//			Coordinate c = new Coordinate(pos.x, pos.y);
//			if (wall.distance(c) < minDist)
//				minDist = (float) wall.distance(c);
//		}
//		return minDist;
//	}

//	public static float shortestDistanceToFeeders(List<Feeder> feeders, Coordinate x) {
//		float minDist = Float.MAX_VALUE;
//		Coordinate p = new Coordinate(x.x, x.y);
//		for (Feeder fn : feeders) {
//			Coordinate pos = fn.getPosition();
//			Coordinate c = new Coordinate(pos.x, pos.y);
//			if (p.distance(c) < minDist)
//				minDist = (float) p.distance(c);
//		}
//		return minDist;
//	}
	
	/**
	 * Get the closest feeder from a list of feeders with their position expressed in local coordinates
	 * @param feeders
	 * @return
	 */
	public static Feeder getClosestFeeder(List<Feeder> feeders,Vector3D pos){
		float minDist = Float.MAX_VALUE;
		Feeder closest = null;
		for (Feeder f : feeders){
			float dist = (float) f.pos.distance(pos);
			if (dist < minDist){
				closest = f;
				minDist = dist;
			}
		}
		return closest;	
	}
	
}
