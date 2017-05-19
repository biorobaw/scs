package edu.usf.experiment.universe;

import java.util.List;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.LineSegment;

public interface WallUniverse {

	public List<Wall> getWalls();

	public float shortestDistanceToWalls(LineSegment wall) ;

	public float wallDistanceToFeeders(LineSegment wall);

	public void addWall(float x, float y, float x2, float y2);

	public float shortestDistanceToWalls(Point2f x1);

	public float shortestDistanceToFeeders(Point2f x);

	public void addWall(LineSegment segment);

	public boolean wallIntersectsOtherWalls(LineSegment wall);

	public float getDistanceToClosestWall(Point3f p);

	public float getDistanceToClosestWall();
	
	public void removeWall(LineSegment wall);

	public void setRevertWallPoint();

	public void revertWalls();
	
	public void clearWalls();

	public float shortestDistanceToRobot(LineSegment wall);

}
