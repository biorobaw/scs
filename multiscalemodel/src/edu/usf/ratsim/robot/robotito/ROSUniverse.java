package edu.usf.ratsim.robot.robotito;

import java.awt.Color;
import java.awt.geom.Rectangle2D.Float;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.FeederDrawer;
import edu.usf.experiment.display.drawer.PlatformDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class ROSUniverse implements GlobalCameraUniverse, WallUniverse, PlatformUniverse, BoundedUniverse, MovableRobotUniverse {

	private static final long SLEEP_PERIOD = 10;
	private LinkedList<Platform> platforms;
	private LinkedList<Wall> walls;
	private LinkedList<Wall> revertWalls;
	private ROSPoseDetector ROSPose;

	
	public ROSUniverse(ElementWrapper params, String logPath) {
		platforms = new LinkedList<Platform>();
		walls = new LinkedList<Wall>();
		revertWalls = new LinkedList<Wall>();
		
		ROSPose = ROSPoseDetector.getInstance();
		
		DisplaySingleton.getDisplay().setupUniversePanel(this);
		DisplaySingleton.getDisplay().addUniverseDrawer(new PlatformDrawer(this));
		DisplaySingleton.getDisplay().addUniverseDrawer(new WallDrawer(this));
		DisplaySingleton.getDisplay().addUniverseDrawer(new RobotDrawer(this));
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
		walls.add(new Wall(segment));
		// TODO: ask to add wall to user
	}

	@Override
	public void addWall(float x, float y, float x2, float y2) {
		walls.add(new Wall(x,y,x2,y2));
		// TODO: ask to add wall to user
	}

	@Override
	public List<Wall> getWalls() {
		return walls;
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
		// Do nothing for now - TODO: ask user to move robot and block
	}

	@Override
	public void setRobotOrientation(float degrees) {
		// Do nothing for now - TODO: ask user to move robot and block		
	}

}
