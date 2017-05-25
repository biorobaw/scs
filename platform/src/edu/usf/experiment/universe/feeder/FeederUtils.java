package edu.usf.experiment.universe.feeder;

import java.util.List;

import javax.vecmath.Point3f;

public class FeederUtils {

	/**
	 * Get the closest feeder from a list of feeders with their position expressed in local coordinates
	 * @param feeders
	 * @return
	 */
	public static Feeder getClosestFeeder(List<Feeder> feeders){
		float minDist = Float.MAX_VALUE;
		Feeder closest = null;
		for (Feeder f : feeders){
			float dist = f.getPosition().distance(new Point3f());
			if (dist < minDist){
				closest = f;
				minDist = dist;
			}
		}
		return closest;	
	}

}
