package edu.usf.experiment.universe;

import java.util.List;

import javax.vecmath.Point3f;

public class FeederUtils {

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
