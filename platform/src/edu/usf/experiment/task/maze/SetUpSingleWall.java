package edu.usf.experiment.task.maze;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpSingleWall extends Task {

	public SetUpSingleWall(ElementWrapper params) {
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
		
		wu.addWall(0, .5f, 0, -.5f);
		
		pu.addPlatform(new Coordinate(-1f, 0), .1f);
	}

}
