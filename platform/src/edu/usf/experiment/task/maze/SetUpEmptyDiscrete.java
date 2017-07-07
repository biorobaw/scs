package edu.usf.experiment.task.maze;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpEmptyDiscrete extends Task {

	private static final float SIZE = 3;

	public SetUpEmptyDiscrete(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s) {
		WallUniverse wu = (WallUniverse) u;
		PlatformUniverse pu = (PlatformUniverse) u;
		
		wu.clearWalls();
		pu.clearPlatforms();
		
		// Outer walls
		wu.addWall(0,0, 0,SIZE);
		wu.addWall(0,SIZE, SIZE,SIZE);
		wu.addWall(SIZE,SIZE, SIZE,0);
		wu.addWall(SIZE,0, 0,0);
		
		pu.addPlatform(new Coordinate(SIZE-1, SIZE-1), 1);
	}
		

}
