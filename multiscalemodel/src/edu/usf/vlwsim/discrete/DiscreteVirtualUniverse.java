package edu.usf.vlwsim.discrete;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.display.drawer.discrete.DiscretePathDrawer;
import edu.usf.experiment.display.drawer.discrete.DiscretePlatformDrawer;
import edu.usf.experiment.display.drawer.discrete.DiscreteRobotDrawer;
import edu.usf.experiment.display.drawer.discrete.GridDrawer;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.GridUniverse;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.ElementWrapper;

public class DiscreteVirtualUniverse
		implements PlatformUniverse, WallUniverse, GlobalCameraUniverse, MovableRobotUniverse, GridUniverse, BoundedUniverse {

	private static final float HALF_CELL = .5f;
	private Robot robot;
	private Coordinate robotPos;
	private float robotAngle;
	private int gridWidth;
	private int gridHeight;
	private Set<Wall> walls;
	private List<Wall> wallsToRevert;
	private List<Platform> platforms;
	private int robotDx;
	private int robotDy;
	
	public DiscreteVirtualUniverse(int gridWidth, int gridHeight, String logPath){
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		
		robotPos = new Coordinate();
		robotAngle = 0;
		
		walls = new ConcurrentSkipListSet<Wall>();
		wallsToRevert = new LinkedList<Wall>();

		platforms = new LinkedList<Platform>();

		robotDx = 0;
		robotDy = 0;

		DisplaySingleton.getDisplay().setupUniversePanel(this);
		DisplaySingleton.getDisplay().addDrawer("universe","platform",new DiscretePlatformDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","grid",new GridDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","walls",new WallDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","path",new DiscretePathDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","robot",new DiscreteRobotDrawer(this));
	}

	public DiscreteVirtualUniverse(ElementWrapper params, String logPath) {
		this(params.getChildInt("width"), params.getChildInt("height"), logPath);
	}

	@Override
	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void setRobotPosition(Coordinate newPos) {
		robotPos = new Coordinate(newPos);
	}

	@Override
	public void setRobotOrientation(float degrees) {
		robotAngle = (float) ((degrees + Math.PI * 2) % (2 * Math.PI));
	}

	@Override
	public Coordinate getRobotPosition() {
		return new Coordinate(robotPos.x, robotPos.y);
	}

	@Override
	public float getRobotOrientationAngle() {
		return robotAngle;
	}

	@Override
	public void addWall(LineSegment segment) {
		Wall w = new Wall(segment);
		walls.add(w);
		wallsToRevert.add(w);
	}

	@Override
	public void addWall(float x, float y, float x2, float y2) {
		Wall w = new Wall(x, y, x2, y2);
		walls.add(w);
		wallsToRevert.add(w);
	}

	@Override
	public Set<Wall> getWalls() {
		return walls;
	}

	@Override
	public void setRevertWallPoint() {
		wallsToRevert.clear();
	}

	@Override
	public void revertWalls() {
		for (Wall w : wallsToRevert)
			walls.remove(w);

		wallsToRevert.clear();
	}

	@Override
	public void clearWalls() {
		walls.clear();
		wallsToRevert.clear();
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
	public void addPlatform(Coordinate pos, float radius, Color color) {
		platforms.add(new Platform(pos, radius, color));
	}

	@Override
	public void addPlatform(Coordinate pos, float radius) {
		addPlatform(pos, radius, Color.YELLOW);
	}

	@Override
	public int getGridWidth() {
		return gridWidth;
	}

	@Override
	public int getGridHeight() {
		return gridHeight;
	}

	/****************************************
	 * Simulation function
	 **************************************/
	public void moveRobot(int dx, int dy) {
		robotPos = new Coordinate(Math.round(robotPos.x + dx), Math.round(robotPos.y + dy));
	}

	@Override
	public void step() {
		if (canRobotMove(robotDx, robotDy))
			moveRobot(robotDx, robotDy);
	}

	public void setMotion(int dx, int dy) {
		robotDx = dx;
		robotDy = dy;
	}

	public boolean canRobotMove(int dx, int dy) {
		Coordinate pos = new Coordinate(robotPos.x + HALF_CELL, robotPos.y + HALF_CELL);
		Coordinate newPos = new Coordinate(robotPos.x + HALF_CELL + dx, robotPos.y + HALF_CELL + dy);
		LineSegment path = new LineSegment(pos, newPos);

		boolean intersects = false;
		for (Wall w : walls)
			intersects |= w.intersects(path);

		return !intersects;
	}

	@Override
	public java.awt.geom.Rectangle2D.Float getBoundingRect() {
		return new Rectangle2D.Float(0, 0, getGridWidth(), getGridHeight());
	}

	@Override
	public void setBoundingRect(java.awt.geom.Rectangle2D.Float boundingRect) {
		
	}

	@Override
	public void clearState() {
		
	}

}
