package edu.usf.experiment.robot;

import java.util.List;

import javax.vecmath.Point3f;

public interface WallRobot extends VisionRobot {
	
	public abstract List<Point3f> getVisibleWallEnds();

	public float getDistanceToClosestWall();
}
