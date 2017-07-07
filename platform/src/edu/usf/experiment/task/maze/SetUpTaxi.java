package edu.usf.experiment.task.maze;

import java.awt.Color;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

public class SetUpTaxi extends Task {

	private static final float SIZE = 20;

	public SetUpTaxi(ElementWrapper params) {
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
		
		wu.addWall(SIZE/5,0, SIZE/5, SIZE/5 * 2);
		wu.addWall(SIZE/5*2,SIZE, SIZE/5*2,SIZE/5*3);
		wu.addWall(SIZE/5*3, 0, SIZE/5*3, SIZE/5*2);
		
		int which = RandomSingleton.getInstance().nextInt(4);
		switch (which){
		case 0:
			pu.addPlatform(new Coordinate(SIZE/20 * 12, 0), 1, Color.BLUE); 
			break;
		case 1:
			pu.addPlatform(new Coordinate(0, 0), 1, Color.YELLOW);
			break;
		case 2:
			pu.addPlatform(new Coordinate(0, SIZE -1), 1, Color.RED);
			break;
		case 3:
			pu.addPlatform(new Coordinate(SIZE - 1, SIZE -1), 1, Color.GREEN);
			break;
		}
		
		
		
		
		
	}
		

}
