package edu.usf.experiment.task.maze;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class SetUpSpiralMaze extends Task {

	public SetUpSpiralMaze(ElementWrapper params) {
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
		
		double l = .4;
//		List<Coordinate> coords = new LinkedList<Coordinate>();
//		coords.add(new Coordinate(0, -l));
//		coords.add(new Coordinate(-l, -l));
//		coords.add(new Coordinate(-l, l));
//		coords.add(new Coordinate(l, l));
//		coords.add(new Coordinate(l, -2*l));
//		coords.add(new Coordinate(-2*l, -2*l));
//		coords.add(new Coordinate(-2*l, 2*l));
//		coords.add(new Coordinate(2*l, 2*l));
//		coords.add(new Coordinate(2*l, -3*l));
//		coords.add(new Coordinate(-3*l, -3*l));
		List<Coordinate> coords = new LinkedList<Coordinate>();
		coords.add(new Coordinate(0, -l));
		coords.add(new Coordinate(l, -l));
		coords.add(new Coordinate(l, l));
		coords.add(new Coordinate(-l, l));
		coords.add(new Coordinate(-l, -2*l));
		coords.add(new Coordinate(2*l, -2*l));
		coords.add(new Coordinate(2*l, 2*l));
		coords.add(new Coordinate(-2*l, 2*l));
		coords.add(new Coordinate(-2*l, -3*l));
		coords.add(new Coordinate(3*l, -3*l));
		
		Coordinate prev = coords.remove(0);
		do {
			Coordinate next = coords.remove(0);
			LineSegment seg = new LineSegment(prev,next);
			wu.addWall(seg);
			prev = next;
		} while (!coords.isEmpty());
		
		
		pu.addPlatform(new Point3f(0f, 0f, 0), .05f);
	}

}
