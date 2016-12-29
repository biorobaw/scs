package edu.usf.experiment.task.maze;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpGMaze extends Task {

	public SetUpGMaze(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s) {
		u.clearWalls();
		u.clearPlatforms();
		
		u.addWall(.5f, 0.5f, 1, 0.5f);
		u.addWall(1f, 0.5f, 1, 1);
		u.addWall(1, 1, 0, 1);
		u.addWall(0, 1, 0, 0f);
		u.addWall(0, 0f, .7f, 0f);
		
		u.addPlatform(new Point3f(.5f, .75f, 0), .05f);
	}

}
