package edu.usf.experiment.universe.wall;

import java.util.List;

import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.Universe;

public interface WallUniverse extends Universe {
	
	// Insertion and Deletions
	public void addWall(LineSegment segment);
	
	public void addWall(float x, float y, float x2, float y2);

	public List<Wall> getWalls();
	
	public void setRevertWallPoint();

	public void revertWalls();
	
	public void clearWalls();
	
	

}
