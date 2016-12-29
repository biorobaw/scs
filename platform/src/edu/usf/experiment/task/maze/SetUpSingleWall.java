package edu.usf.experiment.task.maze;

import java.awt.geom.Point2D;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpSingleWall extends Task {

	public SetUpSingleWall(ElementWrapper params) {
		super(params);
	}

	@Override
	public void perform(Universe u, Subject s) {
		u.clearWalls();
		u.clearPlatforms();
		
		u.addWall(0, .5f, 0, -.5f);
		
		u.addPlatform(new Point3f(-1f, 0, 0), .1f);
	}

}
