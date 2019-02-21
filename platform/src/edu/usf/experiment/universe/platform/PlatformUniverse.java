package edu.usf.experiment.universe.platform;

import java.awt.Color;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.universe.Universe;

public interface PlatformUniverse  {

	public List<Platform> getPlatforms();
	
	public void clearPlatforms();
	
	public void addPlatform(Coordinate pos, float radius);

	public void addPlatform(Coordinate point3f, float radius, Color color);

}
