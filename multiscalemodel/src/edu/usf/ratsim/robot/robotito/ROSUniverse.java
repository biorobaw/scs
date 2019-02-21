package edu.usf.ratsim.robot.robotito;

import java.awt.Color;
import java.awt.geom.Rectangle2D.Float;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.display.Display;
import edu.usf.experiment.display.drawer.PlatformDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class ROSUniverse extends Universe implements GlobalCameraUniverse, WallUniverse, PlatformUniverse, BoundedUniverse, MovableRobotUniverse {

	private static final long SLEEP_PERIOD = 10;
	private LinkedList<Platform> platforms;
	private Set<Wall> walls;
	private LinkedList<Wall> revertWalls;
	private ROSPoseDetector ROSPose;
	private ROSWallDetector wallDetector;

	
	public ROSUniverse(ElementWrapper params, String logPath) {
		platforms = new LinkedList<Platform>();
		walls = new ConcurrentSkipListSet<Wall>();
		revertWalls = new LinkedList<Wall>();
		
		ROSPose = ROSPoseDetector.getInstance();
		wallDetector = ROSWallDetector.getInstance();
		
		Display.getDisplay().setupUniversePanel(this);
		Display.getDisplay().addDrawer("universe","platform",new PlatformDrawer(this));
		Display.getDisplay().addDrawer("universe","walls",new WallDrawer(this));
		Display.getDisplay().addDrawer("universe","robot",new RobotDrawer(this));
	}

	@Override
	public void step() {
		try {
			Thread.sleep(SLEEP_PERIOD);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setRobot(Robot robot) {
		
	}

	@Override
	public List<Platform> getPlatforms() {
		return platforms;
	}

	@Override
	public void clearPlatforms() {
		platforms.clear();
	}

	@Override
	public void addPlatform(Coordinate pos, float radius) {
		addPlatform(pos, radius, Color.ORANGE);
	}

	@Override
	public void addPlatform(Coordinate pos, float radius, Color color) {
		platforms.add(new Platform(pos, radius, color));
	}

	@Override
	public void addWall(LineSegment segment) {
//		walls.add(new Wall(segment));
		// TODO: ask to add wall to user
	}

	@Override
	public void addWall(float x, float y, float x2, float y2) {
//		walls.add(new Wall(x,y,x2,y2));
		// TODO: ask to add wall to user
	}

	@Override
	public Set<Wall> getWalls() {
		return new HashSet<Wall>(wallDetector.getWalls());
	}

	@Override
	public void setRevertWallPoint() {
		revertWalls.clear();
	}

	@Override
	public void revertWalls() {
		walls.removeAll(revertWalls);
	}

	@Override
	public void clearWalls() {
		walls.clear();
	}

	@Override
	public Coordinate getRobotPosition() {
		return ROSPose.getPosition();
	}

	@Override
	public float getRobotOrientationAngle() {
		return ROSPose.getAngle();
	}

	@Override
	public Float getBoundingRect() {
		return new Float(-2.5f, -2.5f, 5f, 5f);
	}

	@Override
	public void setBoundingRect(Float boundingRect) {
		
	}

	@Override
	public void setRobotPosition(Coordinate snewPos) {
		System.out.println("[+] Please move the robot to " + snewPos + " and press enter");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setRobotOrientation(float degrees) {
		System.out.println("[+] Please rotate the robot to " + degrees + " and press enter");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void clearState() {
		
	}

}
