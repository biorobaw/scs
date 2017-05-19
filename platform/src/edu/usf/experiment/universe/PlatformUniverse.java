package edu.usf.experiment.universe;

import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.LineSegment;

public interface PlatformUniverse extends Universe {

	public List<Platform> getPlatforms();
	
	public void clearPlatforms();
	
	public void addPlatform(Point3f pos, float radius);

	public boolean hasRobotFoundPlatform();
	
	public float shortestDistanceToPlatforms(LineSegment wall);
}
