package edu.usf.vlwsim.universe;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.display.DisplaySingleton;
import edu.usf.experiment.display.drawer.FeederDrawer;
import edu.usf.experiment.display.drawer.PlatformDrawer;
import edu.usf.experiment.display.drawer.RobotDrawer;
import edu.usf.experiment.display.drawer.WallDrawer;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.universe.BoundedUniverse;
import edu.usf.experiment.universe.GlobalCameraUniverse;
import edu.usf.experiment.universe.MovableRobotUniverse;
import edu.usf.experiment.universe.element.MazeElement;
import edu.usf.experiment.universe.element.MazeElementLoader;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.platform.PlatformUniverse;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.universe.wall.WallUniverse;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RigidTransformation;
import edu.usf.ratsim.support.XMLDocReader;

/**
 * This universe class creates a universe from an XML file and exposes
 * functionalities needed for performing experiments.
 * 
 * @author ludo
 * 
 */
public abstract class VirtUniverse implements FeederUniverse, PlatformUniverse, WallUniverse, GlobalCameraUniverse,
		BoundedUniverse, MovableRobotUniverse {

	/**
	 * Singleton instance for the universe
	 */
	private static VirtUniverse instance = null;

	/**
	 * How close has food to be to consider it available to the agent
	 */
	private  float CLOSE_TO_FOOD_THRS;
	/**
	 * How far one end of a wall hast to be to consider it an open end (not part
	 * of a biger wall)
	 */
	private final float OPEN_END_THRS = 0.1f;
	/**
	 * Minimum distance the agent must be away from walls
	 */
	private final double MIN_DISTANCE_TO_WALLS = 0.05;

	/**
	 * The robot object for accounting reasons - e.g. position tracking
	 */
	private RigidTransformation robotPos;

	/**
	 * Feeder data
	 */
	private static Map<Integer, Feeder> feeders;

	/**
	 * Wall data
	 */
	private Set<Wall> walls;
	private List<Wall> wallsToRevert;

	/**
	 * Platform data
	 */
	private List<Platform> platforms;

	/**
	 * Bounding rect data
	 */
	private Rectangle2D.Float boundingRect;

	private boolean robotTriedToEat;

	private boolean robotAte;

	private Robot robot;

	private boolean robotWantsToEat;

	private float deltaT;

	/**
	 * Main constructor for the simulator universe
	 * 
	 * @param params
	 *            the parameters in the form of an XML node
	 * @param logPath
	 *            the path to where to log
	 */
	public VirtUniverse(ElementWrapper params, String logPath) {
		CLOSE_TO_FOOD_THRS = params.getChildFloat("closeToFoodThrs");
		setDeltaT(params.getChildFloat("deltaT"));

		walls = new ConcurrentSkipListSet<Wall>();
		feeders = new HashMap<Integer, Feeder>();

		// Assumes maze is already copied by pre-experiment or experiment
		String mazeFile = logPath + "maze.xml";
		Document doc = XMLDocReader.readDocument(mazeFile);
		ElementWrapper maze = new ElementWrapper(doc.getDocumentElement());
		List<ElementWrapper> list;


		robotPos = new RigidTransformation();

		robotWantsToEat = false;

		list = maze.getChildren("wall");
		for (ElementWrapper wall : list) {
			Wall w = new Wall(wall.getChildFloat("x1"), wall.getChildFloat("y1"), wall.getChildFloat("x2"),
					wall.getChildFloat("y2"));
			walls.add(w);

		}
		wallsToRevert = new LinkedList<Wall>();

		MazeElementLoader meloader = MazeElementLoader.getInstance();
		list = maze.getChildren("mazeElement");
		for (ElementWrapper element : list) {
			MazeElement e = meloader.load(element);
			for (Wall w : e.walls) {
				walls.add(w);
			}
		}

		list = maze.getChildren("feeder");
		int i = 0;
		for (ElementWrapper feeder : list) {
			Feeder f = new Feeder(feeder.getChildInt("id"),
					new Coordinate(feeder.getChildFloat("x"), feeder.getChildFloat("y")));
			feeders.put(f.getId(), f);
			i++;
		}

		list = maze.getChildren("platform");
		list = maze.getChildren("platform");
		platforms = new LinkedList<Platform>();
		for (ElementWrapper platform : list) {
			Platform p = new Platform(new Coordinate(platform.getChildFloat("x"), platform.getChildFloat("y")),
					platform.getChildFloat("r"), Color.YELLOW);
			platforms.add(p);
		}

		instance = this;

		robotTriedToEat = false;
		robotAte = false;

		
		ElementWrapper brEW = maze.getChild("boundingRect");
		if(brEW.hasChild("x"))
			setBoundingRect(new Rectangle2D.Float(brEW.getChildFloat("x"), brEW.getChildFloat("y"), brEW.getChildFloat("w"),
					brEW.getChildFloat("h")));
		else {
			float mx=Float.MAX_VALUE, Mx=-Float.MAX_VALUE;
			float my=Float.MAX_VALUE, My=-Float.MAX_VALUE;
			for(Wall w : getWalls()) {
				my = (float)Math.min(my, Math.min(w.s.p0.y, w.s.p1.y));
				My = (float)Math.max(My, Math.max(w.s.p0.y, w.s.p1.y));
				mx = (float)Math.min(mx, Math.min(w.s.p0.x, w.s.p1.x));
				Mx = (float)Math.max(Mx, Math.max(w.s.p0.x, w.s.p1.x));
			}
			setBoundingRect(new Rectangle2D.Float(mx, my, Mx-mx, My-my));
		}
		
		
		DisplaySingleton.getDisplay().setupUniversePanel(this);
		DisplaySingleton.getDisplay().addDrawer("universe","platform",new PlatformDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","feeder",new FeederDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","walls",new WallDrawer(this));
		DisplaySingleton.getDisplay().addDrawer("universe","robot",new RobotDrawer(this));

	}

	@Override
	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	/*********************************
	 * Feeder Universe
	 *************************************/
	// Insertion and Deletion

	public void addFeeder(int id, float x, float y) {
		feeders.put(id, new Feeder(id, new Coordinate(x, y)));
	}

	public Feeder getFeeder(int i) {
		return feeders.get(i);
	}

	public List<Feeder> getFeeders() {
		return new LinkedList<Feeder>(feeders.values());
	}

	// Modifiers
	public void setActiveFeeder(int i, boolean val) {
		feeders.get(i).setActive(val);
	}

	public void setFlashingFeeder(int i, boolean flashing) {
		feeders.get(i).setFlashing(flashing);
	}

	public void setEnableFeeder(Integer f, boolean enabled) {
		feeders.get(f).setEnabled(enabled);
	}

	public void setPermanentFeeder(Integer id, boolean b) {
		feeders.get(id).setPermanent(b);
	}

	public void releaseFood(int feeder) {
		feeders.get(feeder).releaseFood();
	}

	public void clearFoodFromFeeder(Integer f) {
		feeders.get(f).clearFood();
	}

	@Override
	public float getCloseThrs() {
		return CLOSE_TO_FOOD_THRS;
	}
	
	public void setCloseThrs(float thres){
		CLOSE_TO_FOOD_THRS = thres;
	}

	// Simulation methods
	public void robotEat() {
		robotTriedToEat = true;
		int feedingFeeder = -1;

		Coordinate robotPos = getRobotPosition();
		for (Feeder f : feeders.values()) {
			if (robotPos.distance(f.getPosition()) <= CLOSE_TO_FOOD_THRS)
				if (f.hasFood())
					feedingFeeder = f.getId();
		}

		if (feedingFeeder != -1) {
			feeders.get(feedingFeeder).clearFood();
			robotAte = true;
			if (Debug.printRobotAte)
				System.out.println("Robot has eaten");

			((FeederRobot) robot).setAte();
		} else {
			robotAte = false;
			if (Debug.printRobotAte)
				System.out.println("Robot tried to eat far from food");
		}

		robotWantsToEat = false;
	}

	@Override
	public boolean hasRobotEaten() {
		return robotAte;
	}

	@Override
	public boolean hasRobotTriedToEat() {
		return robotTriedToEat;
	}

	/**
	 * This method allows the virtual robot to inform the universe that the
	 * robot wants to eat in the next step
	 */
	public void setRobotEat() {
		robotWantsToEat = true;
	}

	/*********************************
	 * Bounded Universe
	 *************************************/
	public Rectangle2D.Float getBoundingRect() {
		return boundingRect;
	}

	public void setBoundingRect(Rectangle2D.Float boundingRect) {
		this.boundingRect = boundingRect;
	}

	/*********************************
	 * Wall Universe
	 *************************************/
	// Insertion and Deletions
	public void addWall(float x, float y, float x2, float y2) {
		Wall wall = new Wall(x, y, x2, y2);
		wallsToRevert.add(wall);
		walls.add(wall);
	}

	public void addWall(LineSegment segment) {
		Wall wall = new Wall(segment);
		wallsToRevert.add(wall);
		walls.add(wall);
	}

	public Set<Wall> getWalls() {
		return walls;
	}

	public void setRevertWallPoint() {
		wallsToRevert.clear();
	}

	public void revertWalls() {
		for (Wall w : wallsToRevert)
			walls.remove(w);
	}

	public void clearWalls() {
		walls.clear();
	}

	/*********************************
	 * Platform Universe
	 *************************************/
	public List<Platform> getPlatforms() {
		return platforms;
	}

	public void clearPlatforms() {
		platforms.clear();
	}

	public void addPlatform(Coordinate pos, float radius) {
		addPlatform(pos, radius, Color.YELLOW);
	}

	public void addPlatform(Coordinate pos, float radius, Color color) {
		platforms.add(new Platform(pos, radius, color));
	}

	/*********************************
	 * Global Camera Universe
	 *************************************/
	public Coordinate getRobotPosition() {
		return robotPos.getTranslation();
	}

	public float getRobotOrientationAngle() {
		return robotPos.getRotation();
	}

	/*********************************
	 * Movable Robot Universe
	 *************************************/
	public void setRobotPosition(Coordinate pos) {
		robotPos = new RigidTransformation(pos, robotPos.getRotation());

		robotAte = robotTriedToEat = false;
	}

	public void setRobotOrientation(float angle) {
		robotPos = new RigidTransformation(robotPos.getTranslation(), angle);
	}

	/*********************************
	 * Simulation Functions
	 *************************************/
	public float getDeltaT() {
		return deltaT;
	}

	public void setDeltaT(float deltaT) {
		this.deltaT = deltaT;
	}

	/**
	 * This method process a simulation step. In this universe only eat actions
	 * are processed. Motion actions are delegated to specific Universes.
	 */
	@Override
	public void step() {
		if (robotWantsToEat) {
			robotEat();
		} else {
			stepMotion();
		}
	}

	/**
	 * This function processes a simulation step for motion (non-eat) actions.
	 */
	public abstract void stepMotion();

	/**
	 * Move the virtual robot a certain amount of distance
	 * 
	 * @param vector
	 *            Direction and magnitude of the movement
	 */
	public void moveRobot(Coordinate vector) {
		Coordinate from = robotPos.getTranslation();

		// Create a new transforme with the translation
		RigidTransformation trans = new RigidTransformation(vector, 0);
		RigidTransformation toT = new RigidTransformation(robotPos);
		toT.composeBefore(trans);
		Coordinate to = toT.getTranslation();

		// Check for walls in the way
		LineSegment toTravel = new LineSegment(new Coordinate(from.x, from.y), new Coordinate(to.x, to.y));
		boolean tooClose = false;
		for (Wall w : getWalls()) {
			tooClose |= toTravel.distance(w.s) < MIN_DISTANCE_TO_WALLS;
			if (tooClose) {
				break;
			}
		}

		if (!tooClose) {
			// Set the new transform
			robotPos = toT;
		}

		robotAte = robotTriedToEat = false;
	}

	public void rotateRobot(float angle) {
		robotPos.composeBefore(new RigidTransformation(angle));

		robotAte = robotTriedToEat = false;
	}

	public boolean canRobotMove(float angle, float step) {
		RigidTransformation move = new RigidTransformation(step, 0f, angle);
		RigidTransformation to = new RigidTransformation(robotPos);
		to.composeBefore(move);
		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(robotPos.getTranslation(), to.getTranslation());
		for (Wall wall : getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}

	public boolean canRobotMoveAbsAngle(float angle, float step) {
		RigidTransformation move = new RigidTransformation(step, 0f, angle);
		Coordinate to = robotPos.getTranslation();
		move.transform(to, to);

		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(robotPos.getTranslation(), to);
		for (Wall wall : getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}

	public boolean canRobotSeeFeeder(Integer fn, float halfFieldOfView, float visionDist) {
		float angleToFeeder = angleToFeeder(fn);
		boolean inField = angleToFeeder <= halfFieldOfView;
		// System.out.println(fn + " " + angleToFeeder);

		boolean intersects = false;
		Coordinate rPos = new Coordinate(getRobotPosition().x, getRobotPosition().y);
		Coordinate fPos = feeders.get(fn).getPosition();
		LineSegment lineOfSight = new LineSegment(rPos, fPos);
		for (Wall w : getWalls())
			intersects = intersects || w.intersects(lineOfSight);

		boolean closeEnough = getRobotPosition().distance(new Coordinate(feeders.get(fn).getPosition())) < visionDist;

		return inField && !intersects && closeEnough;
	}

	/**
	 * Returns the absolute angle to the feeder
	 * 
	 * @param fn
	 * @return
	 */
	private float angleToFeeder(Integer fn) {
		return Math.abs(GeomUtils.relativeAngleToPoint(getRobotPosition(), getRobotOrientationAngle(),
				feeders.get(fn).getPosition()));

	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();

		// System.out.println("Finalizing Virtual Universe");
	}

	public static VirtUniverse getInstance() {
		return instance;
	}

	public List<Coordinate> getVisibleWallEnds(float halfFieldOfView, float visionDist) {
		List<Coordinate> openEnds = new LinkedList<Coordinate>();
		List<Wall> innerWalls = new LinkedList<Wall>(getWalls());
		// innerWalls.removeAll(initialWalls);
		for (Wall w : innerWalls) {
			Coordinate p = new Coordinate((float) w.s.p0.x, (float) w.s.p0.y);

			float minDist = Float.MAX_VALUE;
			for (Wall w2 : getWalls()) {
				if (w2 != w) {
					if (w2.distanceTo(p) < minDist)
						minDist = w2.distanceTo(p);
				}
			}
			if (minDist > OPEN_END_THRS)
				openEnds.add(p);

			p = new Coordinate((float) w.s.p1.x, (float) w.s.p1.y);
			minDist = Float.MAX_VALUE;
			for (Wall w2 : getWalls()) {
				if (w2 != w) {
					if (w2.distanceTo(p) < minDist)
						minDist = w2.distanceTo(p);
				}
			}
			if (minDist > OPEN_END_THRS)
				openEnds.add(p);
		}

		List<Coordinate> visibleEnds = new LinkedList<Coordinate>();
		for (Coordinate oe : openEnds)
			if (pointCanBeSeenByRobot(oe, halfFieldOfView, visionDist))
				visibleEnds.add(oe);
		return visibleEnds;
	}

	private boolean pointCanBeSeenByRobot(Coordinate p, float halfFieldOfView, float visionDist) {
		boolean inField = false, closeEnough = false, intersects = true;

		float angleToPoint = GeomUtils.relativeAngleToPoint(getRobotPosition(), getRobotOrientationAngle(), p);
		inField = Math.abs(angleToPoint) <= halfFieldOfView;

		if (inField) {
			closeEnough = getRobotPosition().distance(p) < visionDist;

			if (closeEnough) {
				intersects = false;
				Coordinate rPos = new Coordinate(getRobotPosition().x, getRobotPosition().y);
				Coordinate fPos = new Coordinate(p.x, p.y);
				LineSegment lineOfSight = new LineSegment(rPos, fPos);
				for (Wall w : getWalls())
					intersects = intersects
							|| (w.s.p0.distance(fPos) != 0 && w.s.p1.distance(fPos) != 0 && w.intersects(lineOfSight));
			}
		}
		// System.out.println(inField + " " + !intersects + " " + closeEnough);
		return inField && !intersects && closeEnough;
	}

	/**
	 * Returns the reading of the sonar by emulating numRays rays that hit the
	 * obstacles
	 * 
	 * @param angle
	 *            The angle of the sonar in the robot's frame of reference
	 * @param sonarAperture
	 *            The aperture of the sonar sensor
	 * @param maxDist
	 *            The maximum distance that can be sensed
	 * @param numRays
	 *            The number of rays to use. Must be at least two
	 * @param robot_radius
	 *            The radius of the robot. Sonars are assumed to be on the
	 *            periphery of the robot
	 * @return
	 */
	public float getRobotSonarReading(float angle, float sonarAperture, float maxDist, int numRays,
			float robot_radius) {
		// Working data
		Coordinate rPos = getRobotPosition();

		float closestDist = maxDist;
		float robotAngle = getRobotOrientationAngle();

		// For each ray
		for (int i = 0; i < numRays; i++) {
			// Get the angle of the ray in the robot's frame of reference
			// It goes in the segment [angle - aperture/2, angle + aperture/2]
			float rayAngle = angle - sonarAperture / 2 + ((float) i) / (numRays - 1) * sonarAperture;
			float absRayAngle = rayAngle + robotAngle;
			// Sensor coordinate - Sonars are assumed to be on the periphery of
			// the robot
			Coordinate rayStart = new Coordinate(rPos.x + Math.cos(absRayAngle) * robot_radius,
					rPos.y + Math.sin(absRayAngle) * robot_radius);
			// Create each point in the robot frame of reference
			Coordinate rayEnd = new Coordinate(rPos.x + Math.cos(absRayAngle) * (robot_radius + maxDist),
					rPos.y + Math.sin(absRayAngle) * (robot_radius + maxDist));
			// Create a segment from the robot to the point
			LineSegment ray = new LineSegment(rayStart, rayEnd);
			// Intersect the segment to all walls to find closest point to the
			// robot
			for (Wall w : getWalls()) {
				Coordinate intersection = w.s.intersection(ray);
				if (intersection != null) {
					float dist = (float) intersection.distance(rayStart);
					if (dist < closestDist)
						closestDist = dist;
				}

			}
		}

		return closestDist;
	}

}
