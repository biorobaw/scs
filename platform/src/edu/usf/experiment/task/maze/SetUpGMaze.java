package edu.usf.experiment.task.maze;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.PlatformUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpGMaze extends Task {

	public SetUpGMaze(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s) {
		if (!(u instanceof WallUniverse))
			throw new IllegalArgumentException("");
		
		WallUniverse wu = (WallUniverse) u;
		
		if (!(u instanceof PlatformUniverse))
			throw new IllegalArgumentException("");
		
		PlatformUniverse pu = (PlatformUniverse) u;
		
		wu.clearWalls();
		pu.clearPlatforms();
		
		wu.addWall(-.3f, 0f, 1, 0f);
		wu.addWall(1, 0f, 1, 1);
		wu.addWall(1, 1, -1, 1);
		wu.addWall(-1, 1, -1, -1f);
		wu.addWall(-1, -1f, .5f, -1f);
		
		pu.addPlatform(new Point3f(0f, .5f, 0), .1f);
	}

}
