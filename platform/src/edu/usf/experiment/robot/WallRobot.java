package edu.usf.experiment.robot;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public interface WallRobot extends VisionRobot {
	
	public abstract List<Coordinate> getVisibleWallEnds();

	public float getDistanceToClosestWall();
}
