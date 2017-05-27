package edu.usf.vlwsim.universe;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Locale;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.w3c.dom.Document;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.display.DisplaySingleton;
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
import edu.usf.ratsim.support.XMLDocReader;
import edu.usf.vlwsim.display.drawingUtilities.DrawingFunction;
import edu.usf.vlwsim.display.j3d.CylinderNode;
import edu.usf.vlwsim.display.j3d.DirectionalLightNode;
import edu.usf.vlwsim.display.j3d.FeederNode;
import edu.usf.vlwsim.display.j3d.PlatformNode;
import edu.usf.vlwsim.display.j3d.RobotNode;
import edu.usf.vlwsim.display.j3d.ViewNode;
import edu.usf.vlwsim.display.j3d.WallNode;
import edu.usf.vlwsim.display.swing.VirtualUniversePanel;

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
	private final float CLOSE_TO_FOOD_THRS;
	/**
	 * How far one end of a wall hast to be to consider it an open end (not part
	 * of a biger wall)
	 */
	private final float OPEN_END_THRS = 0.1f;
	/**
	 * Minimum distance the agent must be away from walls
	 */
	private final double MIN_DISTANCE_TO_WALLS = 0.025;

	/**
	 * The robot object for accounting reasons - e.g. position tracking
	 */
	private Transform3D robotPos;

	/**
	 * Feeder data
	 */
	private static Map<Integer, Feeder> feeders;

	/**
	 * Wall data
	 */
	private List<Wall> walls;
	private List<Wall> wallsToRevert;

	/**
	 * Platform data
	 */
	private List<Platform> platforms;

	/**
	 * Bounding rect data
	 */
	private Rectangle2D.Float boundingRect;

	// Display information
	/**
	 * This boolean is read from the xml and controls whether the 3d panel
	 * should be used. The user has to additionally specify -display in the
	 * execution to get a display.
	 * If -display is specified but this boolean is not set, the default 2d version is used
	 */
	private boolean display;
	/**
	 * The main branch group
	 */
	private BranchGroup bg;

	/**
	 * The visual node for the robot
	 */
	private RobotNode robotNode;

	/**
	 * Visual nodes for feeders
	 */
	private Map<Integer, FeederNode> feederNodes;

	/**
	 * Visual nodes for walls
	 */
	private List<WallNode> wallNodes;
	private List<WallNode> wallNodesToRevert;

	/**
	 * Visual nodes for platforms
	 */
	private LinkedList<PlatformNode> platformNodes;

	/**
	 * The top view node
	 */
	private View topView;

	private boolean robotTriedToEat;

	private boolean robotAte;

	private Robot robot;

	private boolean robotWantsToEat;

	private float deltaT;

	private List<DrawingFunction> drawingFunctions;

	private Canvas3D topViewCanvas;

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

		walls = new LinkedList<Wall>();
		feeders = new HashMap<Integer, Feeder>();

		display = params.getChildBoolean("display");

		// Assumes maze is already copied by pre-experiment or experiment
		String mazeFile = logPath + "maze.xml";
		Document doc = XMLDocReader.readDocument(mazeFile);
		ElementWrapper maze = new ElementWrapper(doc.getDocumentElement());
		List<ElementWrapper> list;

		ElementWrapper brEW = maze.getChild("boundingRect");
		setBoundingRect(new Rectangle2D.Float(brEW.getChildFloat("x"), brEW.getChildFloat("y"), brEW.getChildFloat("w"),
				brEW.getChildFloat("h")));

		robotPos = new Transform3D();

		robotWantsToEat = false;
		
		if (display) {
			VirtualUniverse vu = new VirtualUniverse();
			Locale l = new Locale(vu);

			bg = new BranchGroup();
			bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
			bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
			bg.setCapability(BranchGroup.ALLOW_DETACH);
			l.addBranchGraph(bg);

			// Add previously created elements, but not added to the 3d universe
			robotNode = new RobotNode(maze.getChild("robotview"), display);
			bg.addChild(robotNode);

			ElementWrapper floor = maze.getChild("floor");
			if (floor != null)
				bg.addChild(new CylinderNode(floor));

			// Top view
			ElementWrapper tv = maze.getChild("topview");
			ViewNode vn = new ViewNode(tv);
			topView = vn.getView();
			bg.addChild(vn);

			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 0f, -5), new Color3f(1f, 1f, 1f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 0f, 5), new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5f, -5), new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 5f, -5), new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5f, 5), new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 5f, 5), new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5, 0), new Color3f(1f, 1f, 1f)));

			// frame = new UniverseFrame(this);
			// frame.setVisible(true);
		}

		// Walls
		list = maze.getChildren("wall");
		wallNodes = new LinkedList<WallNode>();
		for (ElementWrapper wall : list) {
			Wall w = new Wall(wall.getChildFloat("x1"), wall.getChildFloat("y1"), wall.getChildFloat("x2"),
					wall.getChildFloat("y2"));
			walls.add(w);

			if (display) {
				WallNode wn = new WallNode(wall);
				wallNodes.add(wn);
				bg.addChild(wn);
			}
		}
		wallsToRevert = new LinkedList<Wall>();
		wallNodesToRevert = new LinkedList<WallNode>();

		MazeElementLoader meloader = MazeElementLoader.getInstance();
		list = maze.getChildren("mazeElement");
		for (ElementWrapper element : list) {
			MazeElement e = meloader.load(element);
			for (Wall w : e.walls) {
				walls.add(w);

				if (display) {
					WallNode wn = new WallNode(w);
					wallNodes.add(wn);
					bg.addChild(wn);
				}
			}

		}

		list = maze.getChildren("feeder");
		feederNodes = new HashMap<Integer, FeederNode>();
		int i = 0;
		for (ElementWrapper feeder : list) {
			Feeder f = new Feeder(feeder.getChildInt("id"),
					new Point3f(feeder.getChildFloat("x"), feeder.getChildFloat("y"), feeder.getChildFloat("z")));
			feeders.put(f.getId(), f);

			if (display) {
				FeederNode fn = new FeederNode(feeder);
				feederNodes.put(fn.getId(), fn);
				bg.addChild(fn);
			}
			i++;
		}

		list = maze.getChildren("platform");
		platformNodes = new LinkedList<PlatformNode>();
		list = maze.getChildren("platform");
		platforms = new LinkedList<Platform>();
		for (ElementWrapper platform : list) {
			Platform p = new Platform(
					new Point3f(platform.getChildFloat("x"), platform.getChildFloat("y"), platform.getChildFloat("z")),
					platform.getChildFloat("r"));
			platforms.add(p);
			if (display) {
				PlatformNode pn = new PlatformNode(platform);
				platformNodes.add(pn);
				bg.addChild(pn);
			}
		}

		instance = this;

		robotTriedToEat = false;
		robotAte = false;

		if (display){
			drawingFunctions = new LinkedList<DrawingFunction>();
			GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
			topViewCanvas = new Canvas3D(config) {
				private static final long serialVersionUID = 2278728176596780651L;

				public void postRender() {

					for (Runnable r : drawingFunctions)
						r.run();

				}
			};
			topView.addCanvas3D(topViewCanvas);
			topViewCanvas.setPreferredSize(new Dimension(600, 600));
			JPanel topViewPanel = new JPanel();
			topViewPanel.add(topViewCanvas);
			DisplaySingleton.getDisplay().addPanel(topViewPanel, 0, 0, 1, 1);
		} else {
			DisplaySingleton.getDisplay().addPanel(new VirtualUniversePanel(this), 1, 0, 1, 1);
		}

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
		feeders.put(id, new Feeder(id, new Point3f(x, y, 0)));

		if (display) {
			FeederNode feeder = new FeederNode(id, x, y);
			feederNodes.put(id, feeder);
			bg.addChild(feeder);
		}
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

		if (display)
			feederNodes.get(i).setActive(val);
	}

	public void setFlashingFeeder(int i, boolean flashing) {
		feeders.get(i).setFlashing(flashing);

		if (display)
			feederNodes.get(i).setFlashing(flashing);
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

	// Simulation methods
	public void robotEat() {
		robotTriedToEat = true;
		int feedingFeeder = -1;

		Point3f robotPos = getRobotPosition();
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

	// Display methods
	@Override
	public void setWantedFeeder(int feeder, boolean wanted) {
		if (display)
			feederNodes.get(feeder).setWanted(wanted);
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

		if (display) {
			WallNode w = new WallNode(x, y, 0, x2, y2, 0, 0.025f);
			wallNodesToRevert.add(w);
			bg.addChild(w);
			wallNodes.add(w);
		}
	}

	public void addWall(LineSegment segment) {
		Wall wall = new Wall(segment);
		wallsToRevert.add(wall);
		walls.add(wall);

		if (display) {
			WallNode w = new WallNode(segment, 0.025f);
			wallNodesToRevert.add(w);
			bg.addChild(w);
			wallNodes.add(w);
		}
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public void removeWall(LineSegment wall) {
		walls.remove(wall);
	}

	public void setRevertWallPoint() {
		wallsToRevert.clear();
		if (display)
			wallNodesToRevert.clear();
	}

	public void revertWalls() {
		for (Wall w : wallsToRevert)
			walls.remove(w);

		if (display)
			for (WallNode wn : wallNodesToRevert) {
				wallNodes.remove(wn);
				bg.removeChild(wn);
			}
	}

	public void clearWalls() {
		walls.clear();

		for (WallNode wn : wallNodes)
			bg.removeChild(wn);

		wallNodes.clear();
	}

	/*********************************
	 * Platform Universe
	 *************************************/
	public List<Platform> getPlatforms() {
		return platforms;
	}

	public void clearPlatforms() {
		platforms.clear();

		if (display) {
			for (PlatformNode pn : platformNodes)
				bg.removeChild(pn);

			platformNodes.clear();
		}
	}

	public void addPlatform(Point3f pos, float radius) {
		platforms.add(new Platform(pos, radius));

		if (display) {
			PlatformNode p = new PlatformNode(pos.x, pos.y, radius);
			platformNodes.add(p);
			bg.addChild(p);
		}
	}

	/*********************************
	 * Global Camera Universe
	 *************************************/
	public Point3f getRobotPosition() {
		Transform3D t = new Transform3D(robotPos);
		Vector3f pos = new Vector3f();
		t.get(pos);

		return new Point3f(pos);
	}

	public Quat4f getRobotOrientation() {
		Transform3D t = new Transform3D(robotPos);
		// Get the rotation from the quaternion
		// http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
		Quat4f rot = new Quat4f();
		t.get(rot);

		return rot;
	}

	public float getRobotOrientationAngle() {
		Transform3D t = new Transform3D(robotPos);
		// Get the rotation from the quaternion
		// http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
		Quat4f rot = new Quat4f();
		t.get(rot);
		return (float) (2 * Math.acos(rot.w) * Math.signum(rot.z));
	}

	/*********************************
	 * Movable Robot Universe
	 *************************************/
	public void setRobotPosition(Point2D.Float pos, float angle) {
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3f(pos.x, pos.y, 0));
		Transform3D rot = new Transform3D();
		rot.rotZ(angle);
		translate.mul(rot);
		robotPos = translate;
		if (display)
			robotNode.getTransformGroup().setTransform(translate);

		robotAte = robotTriedToEat = false;
	}

	public void setRobotOrientation(float angle) {
		Transform3D newOrient = new Transform3D();
		newOrient.rotZ(angle);
		Quat4f rotQuat = new Quat4f();
		newOrient.get(rotQuat);
		robotPos.setRotation(rotQuat);
		if (display)
			robotNode.getTransformGroup().setTransform(robotPos);
	}

	public void rotateRobot(double degrees) {
		// Create a new transforme with the translation
		Transform3D trans = new Transform3D();
		trans.rotZ(degrees);
		// Get the old pos transform
		Transform3D rPos = new Transform3D(robotPos);
		// Apply translation to old transform
		rPos.mul(trans);
		// Set the new transform
		robotPos = rPos;
		if (display)
			robotNode.getTransformGroup().setTransform(rPos);

		robotAte = robotTriedToEat = false;
	}

	/*********************************
	 * Visual Functions
	 *************************************/
	public void addDrawingFunction(DrawingFunction function) {
		if (display) {
			drawingFunctions.add(function);
			function.setGraphics(topViewCanvas.getGraphics2D());
		}
	}

	public View getTopView() {
		return topView;
	}

	public View[] getRobotViews() {
		return robotNode.getRobotViews();
	}

	public Canvas3D[] getRobotOffscreenCanvas() {
		return robotNode.getOffScreenCanvas();
	}

	public ImageComponent2D[] getRobotOffscreenImages() {
		return robotNode.getOffScreenImages();
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
	public void moveRobot(Vector3f vector) {
		Vector3f from = new Vector3f();
		// Get the old pos transform
		Transform3D rPos = new Transform3D(robotPos);
		rPos.get(from);

		// Create a new transforme with the translation
		Transform3D trans = new Transform3D();
		trans.setTranslation(vector);

		// Apply translation to old transform
		rPos.mul(trans);

		// Check for walls in the way
		Vector3f to = new Vector3f();
		rPos.get(to);
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
			robotPos = rPos;

			if (display)
				robotNode.getTransformGroup().setTransform(rPos);
		}

		robotAte = robotTriedToEat = false;
	}

	public boolean canRobotMove(float angle, float step) {
		// The current position with rotation
		Transform3D rPos = new Transform3D(robotPos);

		Vector3f p = new Vector3f();
		rPos.get(p);
		Coordinate initCoordinate = new Coordinate(p.x, p.y);
		// A translation vector to calc affordances
		Transform3D trans = new Transform3D();
		trans.setTranslation(new Vector3f(step, 0f, 0f));
		// The rotatio of the action
		Transform3D rot = new Transform3D();
		rot.rotZ(angle);
		// Apply hipotetical transformations
		rPos.mul(rot);
		rPos.mul(trans);
		// Get the new position
		Vector3f finalPos = new Vector3f();
		rPos.get(finalPos);
		Coordinate finalCoordinate = new Coordinate(finalPos.x, finalPos.y);
		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(initCoordinate, finalCoordinate);
		for (Wall wall : getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}

	public boolean canRobotMoveAbsAngle(float angle, float step) {
		// The current position with rotation
		Transform3D rPos = new Transform3D(robotPos);

		Vector3f p = new Vector3f();
		rPos.get(p);
		Coordinate initCoordinate = new Coordinate(p.x, p.y);
		// A translation vector to calc affordances
		Transform3D trans = new Transform3D();
		trans.setTranslation(new Vector3f(step, 0f, 0f));
		// The set the absolute direction
		rPos.rotZ(angle);
		// Translate step in the new x direction
		rPos.mul(trans);
		// Get the new position
		Vector3f finalPos = new Vector3f();
		rPos.get(finalPos);
		Coordinate finalCoordinate = new Coordinate(finalPos.x, finalPos.y);
		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(initCoordinate, finalCoordinate);
		for (Wall wall : getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}

	public boolean canRobotMoveDeltaPos(Point3f deltaPos) {
		// The current position with rotation

		Vector3f p = new Vector3f();
		robotPos.get(p);

		Coordinate initCoordinate = new Coordinate(p.x, p.y);
		Coordinate finalCoordinate = new Coordinate(p.x + deltaPos.x, p.y + deltaPos.y);

		// System.out.println("movement: "+initCoordinate + " " +
		// finalCoordinate);

		// Check if crosses any wall
		boolean intesectsWall = false;
		LineSegment path = new LineSegment(initCoordinate, finalCoordinate);
		for (Wall wall : getWalls()) {
			intesectsWall = intesectsWall || (path.distance(wall.s) < MIN_DISTANCE_TO_WALLS);
		}

		return !intesectsWall;
	}

	public void dispose() {
		for (FeederNode f : feederNodes.values())
			f.terminate();
	}

	public boolean canRobotSeeFeeder(Integer fn, float halfFieldOfView, float visionDist) {
		float angleToFeeder = angleToFeeder(fn);
		boolean inField = angleToFeeder <= halfFieldOfView;
		// System.out.println(fn + " " + angleToFeeder);

		boolean intersects = false;
		Coordinate rPos = new Coordinate(getRobotPosition().x, getRobotPosition().y);
		Point3f fPosV = feeders.get(fn).getPosition();
		Coordinate fPos = new Coordinate(fPosV.x, fPosV.y);
		LineSegment lineOfSight = new LineSegment(rPos, fPos);
		for (Wall w : getWalls())
			intersects = intersects || w.intersects(lineOfSight);

		boolean closeEnough = getRobotPosition().distance(new Point3f(feeders.get(fn).getPosition())) < visionDist;

		return inField && !intersects && closeEnough;
	}

	/**
	 * Returns the absolute angle to the feeder
	 * 
	 * @param fn
	 * @return
	 */
	private float angleToFeeder(Integer fn) {
		return Math.abs(GeomUtils.angleToPointWithOrientation(getRobotOrientation(), getRobotPosition(),
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

	public List<Point3f> getVisibleWallEnds(float halfFieldOfView, float visionDist) {
		List<Point3f> openEnds = new LinkedList<Point3f>();
		List<Wall> innerWalls = new LinkedList<Wall>(getWalls());
		// innerWalls.removeAll(initialWalls);
		for (Wall w : innerWalls) {
			Point3f p = new Point3f((float) w.s.p0.x, (float) w.s.p0.y, 0f);

			float minDist = Float.MAX_VALUE;
			for (Wall w2 : getWalls()) {
				if (w2 != w) {
					if (w2.distanceTo(p) < minDist)
						minDist = w2.distanceTo(p);
				}
			}
			if (minDist > OPEN_END_THRS)
				openEnds.add(p);

			p = new Point3f((float) w.s.p1.x, (float) w.s.p1.y, 0f);
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

		List<Point3f> visibleEnds = new LinkedList<Point3f>();
		for (Point3f oe : openEnds)
			if (pointCanBeSeenByRobot(oe, halfFieldOfView, visionDist))
				visibleEnds.add(oe);
		return visibleEnds;
	}

	private boolean pointCanBeSeenByRobot(Point3f p, float halfFieldOfView, float visionDist) {
		boolean inField = false, closeEnough = false, intersects = true;

		float angleToPoint = GeomUtils.angleToPointWithOrientation(getRobotOrientation(), getRobotPosition(), p);
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
	 * 
	 * @param bodyToNoseLength
	 * @param distToConsider
	 * @return 0 for no close wall, 1 for left, 2 for right, 3 for both
	 */
	public int isWallCloseToSides(float bodyToNoseLength, float distToConsider) {
		// Find the nose
		Transform3D rPos = new Transform3D(robotPos);
		Transform3D toNose = new Transform3D();
		toNose.setTranslation(new Vector3d(bodyToNoseLength, 0, 0));
		rPos.mul(toNose);
		Transform3D nose = rPos;
		// Find left of nose
		Transform3D leftOfNose = new Transform3D(nose);
		Transform3D rotate90 = new Transform3D();
		rotate90.setRotation(new AxisAngle4f(new Vector3f(0, 0, 1), (float) (Math.PI / 2)));
		leftOfNose.mul(rotate90);
		Transform3D lengthToConsider = new Transform3D();
		lengthToConsider.setTranslation(new Vector3f(distToConsider, 0, 0));
		leftOfNose.mul(lengthToConsider);
		// Find rigth of nose
		Transform3D rightOfNose = new Transform3D(nose);
		Transform3D rotateminus90 = new Transform3D();
		rotateminus90.setRotation(new AxisAngle4f(new Vector3f(0, 0, 1), (float) (-Math.PI / 2)));
		rightOfNose.mul(rotateminus90);
		rightOfNose.mul(lengthToConsider);
		// Intersect with walls
		Vector3f nosePoint = new Vector3f();
		nose.get(nosePoint);
		Coordinate noseCoord = new Coordinate(nosePoint.x, nosePoint.y);
		Vector3f leftOfNosePoint = new Vector3f();
		leftOfNose.get(leftOfNosePoint);
		Coordinate leftOfNoseCoord = new Coordinate(leftOfNosePoint.x, leftOfNosePoint.y);
		Vector3f rightOfNoisePoint = new Vector3f();
		rightOfNose.get(rightOfNoisePoint);
		Coordinate rightOfNoseCoord = new Coordinate(rightOfNoisePoint.x, rightOfNoisePoint.y);

		boolean intersectsLeft = false;
		LineSegment leftSeg = new LineSegment(noseCoord, leftOfNoseCoord);
		for (Wall w : getWalls())
			intersectsLeft |= w.intersects(leftSeg);

		boolean intersectsRight = false;
		LineSegment rightSeg = new LineSegment(noseCoord, rightOfNoseCoord);
		for (Wall w : getWalls())
			intersectsRight |= w.intersects(rightSeg);

		if (!intersectsLeft && !intersectsRight)
			return 0;
		else if (!intersectsLeft)
			return 2;
		else if (!intersectsRight)
			return 1;
		else
			return 3;
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
	 * @return
	 */
	public float getRobotSonarReading(float angle, float sonarAperture, float maxDist, int numRays) {
		// Working data
		Point3f rPos = getRobotPosition();
		Coordinate rCoor = new Coordinate(rPos.x, rPos.y);

		Transform3D rT = robotPos;

		float closestDist = maxDist;

		// For each ray
		for (int i = 0; i < numRays; i++) {
			// Get the angle of the ray in the robot's frame of reference
			// It goes in the segment [angle - aperture/2, angle + aperture/2]
			float rayAngle = angle - sonarAperture / 2 + ((float) i) / (numRays - 1) * sonarAperture;
			// Create each point in the robot frame of reference
			Point3f rayEnd = new Point3f((float) (Math.cos(rayAngle) * maxDist), (float) (Math.sin(rayAngle) * maxDist),
					0);
			// Rotate and translate it to reflect the robot frame of ref
			rT.transform(rayEnd);
			Coordinate rayEndCoor = new Coordinate(rayEnd.x, rayEnd.y);
			// Create a segment from the robot to the point
			LineSegment s = new LineSegment(rCoor, rayEndCoor);
			// Intersect the segment to all walls to find closest point to the
			// robot
			for (Wall w : getWalls()) {
				Coordinate intersection = w.s.intersection(s);
				if (intersection != null) {
					float dist = (float) intersection.distance(rCoor);
					if (dist < closestDist)
						closestDist = dist;
				}

			}
		}

		return closestDist;
	}

}
