package edu.usf.experiment.universe;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.universe.element.MazeElement;
import edu.usf.experiment.universe.element.MazeElementLoader;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.XMLDocReader;

public abstract class Universe {

	

	// TODO: get as param
	public static float CLOSE_TO_FOOD_THRS;

	private static Map<Integer, Feeder> feeders;
	private List<Wall> walls;
	
	private List<Platform> platforms;
	private Rectangle2D.Float boundingRect;

	private int lastAteFeeder;

	private List<Wall> innerWalls;

	private List<Wall> wallsToRevert;

	public Universe(ElementWrapper params, String logPath) {
		CLOSE_TO_FOOD_THRS = params.getChildFloat("closeToFoodThrs");

		walls = new LinkedList<Wall>();
		feeders = new HashMap<Integer, Feeder>();

		// Assumes maze is already copied by pre-experiment or experiment
		String mazeFile = logPath + "maze.xml";
		Document doc = XMLDocReader.readDocument(mazeFile);
		ElementWrapper maze = new ElementWrapper(doc.getDocumentElement());
		List<ElementWrapper> list;

		ElementWrapper brEW = maze.getChild("boundingRect");
		setBoundingRect(new Rectangle2D.Float(brEW.getChildFloat("x"),
				brEW.getChildFloat("y"), brEW.getChildFloat("w"),
				brEW.getChildFloat("h")));

		// list = doc.getElementsByTagName("pool");
		// if (list.getLength() != 0)
		// pool = new PoolNode(list.item(0));

		// Walls
		list = maze.getChildren("wall");
		for (ElementWrapper wall : list) {
			Wall w = new Wall(wall.getChildFloat("x1"),
					wall.getChildFloat("y1"), wall.getChildFloat("x2"),
					wall.getChildFloat("y2"));
			walls.add(w);
		}

		MazeElementLoader meloader = MazeElementLoader.getInstance();
		list = maze.getChildren("mazeElement");
		for (ElementWrapper element : list) {
			MazeElement e = meloader.load(element);
			for (Wall w : e.walls) {
				walls.add(w);
			}

		}

		// ElementWrapper pool = maze.getChild("pool");
		// if (pool != null){
		// float r = pool.getChildFloat("r");
		// float x = pool.getChildFloat("x");
		// float y = pool.getChildFloat("y");
		// float currentAngle = (float) (Math.PI / WALLS_PER_POOL);
		// for (int i = 0; i < WALLS_PER_POOL; i++) {
		// float x1 = (float) (x + r * Math.sin(currentAngle));
		// float y1 = (float) (y + r * Math.cos(currentAngle));
		// float nextAngle = (float) (currentAngle + (2 * Math.PI /
		// WALLS_PER_POOL) % (2*Math.PI));
		// float x2 = (float) (r * Math.sin(nextAngle));
		// float y2 = (float) (r * Math.cos(nextAngle));
		//
		// walls.add(new Wall(x1, y1, x2, y2));
		//
		// currentAngle = nextAngle;
		// }
		// }

		list = maze.getChildren("feeder");
		int i = 0;
		for (ElementWrapper feeder : list) {
			Feeder f = new Feeder(feeder.getChildInt("id"),
					new Point3f(feeder.getChildFloat("x"), feeder.getChildFloat("y"), feeder.getChildFloat("z")));
			feeders.put(f.getId(), f);
			i++;
		}
		wallsToRevert = new LinkedList<Wall>();

		list = maze.getChildren("platform");
		platforms = new LinkedList<Platform>();
		for (ElementWrapper platform : list) {
			Platform p = new Platform(
					new Point3f(platform.getChildFloat("x"), platform.getChildFloat("y"), platform.getChildFloat("z")),
					platform.getChildFloat("r"));
			platforms.add(p);
		}

	}

	// Boundaries
	public Rectangle2D.Float getBoundingRectangle() {
		return boundingRect;
	}

	// Robot position
	public abstract Point3f getRobotPosition();

	public abstract Quat4f getRobotOrientation();

	public abstract float getRobotOrientationAngle();

	// Feeders
	public Point3f getFoodPosition(int i) {
		return feeders.get(i).getPosition();
	}

	public List<Integer> getFlashingFeeders() {
		List<Integer> res = new LinkedList<Integer>();
		for (Feeder f : feeders.values())
			if (f.isFlashing())
				res.add(f.getId());

		return res;
	}

	public static List<Integer> getActiveFeeders() {
		List<Integer> res = new LinkedList<Integer>();
		for (Feeder f : feeders.values())
			if (f.isActive())
				res.add(f.getId());

		return res;
	}

	public int getNumFeeders() {
		return feeders.size();
	}

	public void setActiveFeeder(int i, boolean val) {
		feeders.get(i).setActive(val);
	}

	public void setFlashingFeeder(int i, boolean flashing) {
		feeders.get(i).setFlashing(flashing);
	}

	public List<Integer> getFeederNums() {
		return new LinkedList<Integer>(feeders.keySet());
	}

	public List<Feeder> getFeeders() {
		return new LinkedList<Feeder>(feeders.values());
	}

	public Feeder getFeeder(int i) {
		return feeders.get(i);
	}

	public boolean isFeederActive(int feeder) {
		return feeders.get(feeder).isActive();
	}

	public boolean isFeederFlashing(int feeder) {
		return feeders.get(feeder).isFlashing();
	}

	public void releaseFood(int feeder) {
		feeders.get(feeder).releaseFood();
	}

	public boolean hasFoodFeeder(int feeder) {
		return feeders.get(feeder).hasFood();
	}

	// Involving position and food
	public boolean hasRobotFoundFood() {
		Point3f robot = getRobotPosition();
		for (Feeder f : feeders.values()) {
			if (f.isActive() && f.hasFood() && robot.distance(f.getPosition()) < CLOSE_TO_FOOD_THRS)
				return true;
		}

		return false;
	}

	public boolean robotEat() {
		int feedingFeeder = -1;

		Point3f robotPos = getRobotPosition();
		for (Feeder f : feeders.values()) {
//			System.out.println(""+f.getId() + " " + robotPos.distance(f.getPosition()) + " " + CLOSE_TO_FOOD_THRS + " " + f.hasFood());
			if (robotPos.distance(f.getPosition()) <= CLOSE_TO_FOOD_THRS)
				if (f.hasFood())
					feedingFeeder = f.getId();
		}

		if (feedingFeeder != -1) {
			feeders.get(feedingFeeder).clearFood();
			lastAteFeeder = feedingFeeder;
			if (Debug.printRobotEaten)
				System.out.println("Robot has eaten");
			return true;
		} else {
			System.out.println("Robot tried to eat far from food");
			return false;
		}
	}

	public int getLastFeedingFeeder() {
		return lastAteFeeder;
	}

	public boolean isRobotCloseToFeeder(int currentGoal) {
		Point3f robot = getRobotPosition();
		return robot.distance(feeders.get(currentGoal).getPosition()) < CLOSE_TO_FOOD_THRS;
	}

	public int getFeedingFeeder() {
		Point3f robotPos = getRobotPosition();
		for (Feeder f : feeders.values()) {
			if (f.isActive())
				if (robotPos.distance(f.getPosition()) < CLOSE_TO_FOOD_THRS)
					return f.getId();
		}

		return -1;
	}

	public boolean hasRobotFoundFeeder(int i) {
		Point3f robot = getRobotPosition();
		Feeder f = feeders.get(i);
		return robot.distance(f.getPosition()) < CLOSE_TO_FOOD_THRS;
	}

	public boolean isRobotCloseToAFeeder() {
		Point3f robot = getRobotPosition();
		for (Feeder f : feeders.values())
			if (robot.distance(f.getPosition()) < CLOSE_TO_FOOD_THRS)
				return true;
		return false;
	}

	public float getDistanceToFeeder(int i) {
		return getRobotPosition().distance(feeders.get(i).getPosition());
	}

	public int getFoundFeeder() {
		Point3f robot = getRobotPosition();
		for (Feeder f : feeders.values())
			if (robot.distance(f.getPosition()) < CLOSE_TO_FOOD_THRS)
				return f.getId();

		return -1;
	}

	public static List<Integer> getEnabledFeeders() {
		List<Integer> res = new LinkedList<Integer>();
		for (Feeder f : feeders.values())
			if (f.isEnabled())
				res.add(f.getId());
		return res;
	}

	public void setEnableFeeder(Integer f, boolean enabled) {
		feeders.get(f).setEnabled(enabled);
	}

	public Rectangle2D.Float getBoundingRect() {
		return boundingRect;
	}

	public void setBoundingRect(Rectangle2D.Float boundingRect) {
		this.boundingRect = boundingRect;
	}

	public abstract void setRobotPosition(Point2D.Float float1, float w);

	public List<Wall> getWalls() {
		return walls;
	}

	public float shortestDistanceToWalls(LineSegment wall) {
		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : walls)
			if (w.distanceTo(wall) < shortestDistance)
				shortestDistance = w.distanceTo(wall);

		return shortestDistance;
	}

	public float wallDistanceToFeeders(LineSegment wall) {
		float minDist = Float.MAX_VALUE;
		for (Feeder fn : feeders.values()) {
			Point3f pos = fn.getPosition();
			Coordinate c = new Coordinate(pos.x, pos.y);
			if (wall.distance(c) < minDist)
				minDist = (float) wall.distance(c);
		}
		return minDist;
	}

	public void addWall(float x, float y, float x2, float y2) {
		Wall wall = new Wall(x, y, x2, y2);
		wallsToRevert.add(wall);
		walls.add(wall);
	}

	public float shortestDistanceToWalls(Point2f x1) {
		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : walls)
			if (w.distanceTo(x1) < shortestDistance)
				shortestDistance = w.distanceTo(x1);

		return shortestDistance;
	}

	public float shortestDistanceToFeeders(Point2f x) {
		float minDist = Float.MAX_VALUE;
		Coordinate p = new Coordinate(x.x, x.y);
		for (Feeder fn : feeders.values()) {
			Point3f pos = fn.getPosition();
			Coordinate c = new Coordinate(pos.x, pos.y);
			if (p.distance(c) < minDist)
				minDist = (float) p.distance(c);
		}
		return minDist;
	}

	public void addWall(LineSegment segment) {
		Wall wall = new Wall(segment);
		wallsToRevert.add(wall);
		walls.add(wall);
	}

	public boolean wallIntersectsOtherWalls(LineSegment wall) {
		boolean intersects = false;
		for (Wall w : walls)
			intersects = intersects || w.intersects(wall);

		return intersects;
	}

	public float getDistanceToClosestWall(Point3f p) {
		Point2f p2 = new Point2f(p.x, p.y);

		float shortestDistance = Float.MAX_VALUE;
		for (Wall w : getWalls())
			if (w.distanceTo(p2) < shortestDistance)
				shortestDistance = w.distanceTo(p2);

		return shortestDistance;
	}

	public float getDistanceToClosestWall() {
		return getDistanceToClosestWall(getRobotPosition());
	}

	public boolean isFeederEnabled(int feeder) {
		return feeders.get(feeder).isEnabled();
	}

	public float shortestDistanceToFeeders(LineSegment wall) {
		double distance = Float.MAX_VALUE;
		for (Feeder f : feeders.values()) {
			Coordinate p = new Coordinate(f.getPosition().x, f.getPosition().y);
			if (wall.distance(p) < distance)
				distance = wall.distance(p);
		}
		return (float) distance;
	}

	public void removeWall(LineSegment wall) {
		walls.remove(wall);
	}

	public void setRevertWallPoint() {
		wallsToRevert.clear();
	}

	public void revertWalls() {
		for (Wall w : wallsToRevert)
			walls.remove(w);
	}

	public void clearFoodFromFeeder(Integer f) {
		feeders.get(f).clearFood();
	}

	public void addFeeder(int id, float x, float y) {
		feeders.put(id, new Feeder(id, new Point3f(x, y, 0)));
	}

	public List<Platform> getPlatforms() {
		return platforms;
	}

	public boolean hasRobotFoundPlatform() {
		Point3f pos = getRobotPosition();
		for (Platform plat : platforms)
			if (plat.getPosition().distance(pos) < CLOSE_TO_FOOD_THRS)
				return true;
		return false;
	}
	
	public void clearWalls() {
		walls.clear();
	}

	public float shortestDistanceToRobot(LineSegment wall) {
		return (float) wall.distance(new Coordinate(getRobotPosition().x, getRobotPosition().y));
		
	}
	
	public void addFeeder(Feeder f) {
		feeders.put(f.getId(), f);
	}
	
	public void setPermanentFeeder(Integer id, boolean b) {
		feeders.get(id).setPermanent(b);
	}

}
