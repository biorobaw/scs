package edu.usf.experiment.universe.platform;

import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.Universe;

public interface PlatformUniverse extends Universe {

	public List<Platform> getPlatforms();
	
	public void clearPlatforms();
	
	public void addPlatform(Point3f pos, float radius);

}
