package edu.usf.ratsim.experiment.universe.virtual;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Locale;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.Wall;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.ratsim.support.GeomUtils;
import edu.usf.ratsim.support.XMLDocReader;

/**
 * This universe class creates a universe from an XML file and exposes
 * functionalities needed for performing experiments.
 * 
 * @author ludo
 * 
 */
public class VirtUniverse extends Universe {

	private static final float OPEN_END_THRS = 0.1f;
	private static VirtUniverse instance = null;
	private View topView;
	private RobotNode robotNode;
	private Robot robot;
	private List<FeederNode> feederNodes;

	private BranchGroup bg;

	private BoundingRectNode boundingRect;

	private List<WallNode> wallNodes;
	private boolean display;
	private List<Wall> initialWalls;
	private List<WallNode> wallsToRevert;

	public VirtUniverse(ElementWrapper params, String logPath) {
		super(params, logPath);

		String mazeFile = params.getChildText("maze");
		display = params.getChildBoolean("display");

		wallNodes = new LinkedList<WallNode>();

		// Just initialize the nodes we need
		String dstMazeFile = logPath + "/maze.xml";
		IOUtils.copyFile(mazeFile, dstMazeFile);
		Document doc = XMLDocReader.readDocument(dstMazeFile);
		ElementWrapper maze = new ElementWrapper(doc.getDocumentElement());
		List<ElementWrapper> list;

		robot = new Robot();

		// Save initial walls to differentiate from later on added ones
		initialWalls = new LinkedList<Wall>(getWalls());

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

			// Walls
			for (Wall wn : getWalls()) {
				WallNode w = new WallNode(wn);
				wallNodes.add(w);
				bg.addChild(w);
			}

			list = maze.getChildren("feeder");
			feederNodes = new LinkedList<FeederNode>();
			for (ElementWrapper fn : list) {
				FeederNode feeder = new FeederNode(fn);
				feederNodes.add(feeder);
				bg.addChild(feeder);
			}

			ElementWrapper floor = maze.getChild("floor");
			if (floor != null)
				bg.addChild(new CylinderNode(floor));

			// Top view
			ElementWrapper tv = maze.getChild("topview");
			ViewNode vn = new ViewNode(tv);
			topView = vn.getView();
			bg.addChild(vn);

			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 0f, -5),
					new Color3f(1f, 1f, 1f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 0f, 5),
					new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5f, -5),
					new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 5f, -5),
					new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5f, 5),
					new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, 5f, 5),
					new Color3f(.5f, .5f, .5f)));
			bg.addChild(new DirectionalLightNode(new Vector3f(0f, -5, 0),
					new Color3f(1f, 1f, 1f)));

			UniverseFrame frame = new UniverseFrame(this);
			frame.setVisible(true);
		}

		instance = this;
		
		wallsToRevert = new LinkedList<WallNode>();
	}

	public void addWall(float x1, float y1, float x2, float y2) {
		super.addWall(x1, y1, x2, y2);

		if (display) {
			WallNode w = new WallNode(x1, y1, 0, x2, y2, 0, 0.025f);
			wallsToRevert.add(w);
			bg.addChild(w);
			wallNodes.add(w);
		}
	}

	@Override
	public void setRevertWallPoint() {
		super.setRevertWallPoint();
		
		wallsToRevert.clear();
	}

	@Override
	public void revertWalls() {
		super.revertWalls();
		if (display)
			for (WallNode wn : wallsToRevert){
				wallNodes.remove(wn);
				bg.removeChild(wn);
			}
	}

	public void addWall(LineSegment wSegment) {
		super.addWall(wSegment);

		if (display) {
			WallNode w = new WallNode(wSegment, 0.025f);
			wallsToRevert.add(w);
			bg.addChild(w);
			wallNodes.add(w);
		}
	}

	public void clearWalls() {
		wallNodes.clear();
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

	/**
	 * Return the virtual robot's position
	 * 
	 * @return
	 */
	public Point3f getRobotPosition() {
		Transform3D t = new Transform3D(robot.getT());
		Vector3f pos = new Vector3f();
		t.get(pos);

		return new Point3f(pos);
	}

	/**
	 * Sets the virtual robot position
	 * 
	 * @param vector
	 *            Robots position
	 */

	public void setRobotPosition(Point2D.Float pos, float angle) {
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3f(pos.x, pos.y, 0));
		Transform3D rot = new Transform3D();
		rot.rotZ(angle);
		translate.mul(rot);
		robot.setT(translate);
		if (display)
			robotNode.getTransformGroup().setTransform(translate);
	}

	/**
	 * Rotate the virtual world robot.
	 * 
	 * @param degrees
	 *            Amount to rotate in degrees.
	 */
	public void rotateRobot(double degrees) {
		// Create a new transforme with the translation
		Transform3D trans = new Transform3D();
		trans.rotZ(degrees);
		// Get the old pos transform
		Transform3D rPos = new Transform3D(robot.getT());
		// Apply translation to old transform
		rPos.mul(trans);
		// Set the new transform
		robot.setT(rPos);
		if (display)
			robotNode.getTransformGroup().setTransform(rPos);
	}

	/**
	 * Move the virtual robot a certain amount of distance
	 * 
	 * @param vector
	 *            Direction and magnitude of the movement
	 */
	public void moveRobot(Vector3f vector) {
		// Create a new transforme with the translation
		Transform3D trans = new Transform3D();
		trans.setTranslation(vector);
		// Get the old pos transform
		Transform3D rPos = new Transform3D(robot.getT());
		// Apply translation to old transform
		rPos.mul(trans);
		// Set the new transform
		robot.setT(rPos);
		if (display)
			robotNode.getTransformGroup().setTransform(rPos);
	}

	public Quat4f getRobotOrientation() {
		Transform3D t = new Transform3D(robot.getT());
		// Get the rotation from the quaternion
		// http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
		Quat4f rot = new Quat4f();
		t.get(rot);

		return rot;
	}

	public float getRobotOrientationAngle() {
		Transform3D t = new Transform3D(robot.getT());
		// Get the rotation from the quaternion
		// http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
		Quat4f rot = new Quat4f();
		t.get(rot);
		return (float) (2 * Math.acos(rot.w) * Math.signum(rot.z));
	}

	@Override
	public void setActiveFeeder(int i, boolean val) {
		super.setActiveFeeder(i, val);

		if (display)
			feederNodes.get(i).setActive(val);
	}

	public boolean canRobotMove(float angle, float step) {
		// The current position with rotation
		Transform3D rPos = new Transform3D(robot.getT());

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
			intesectsWall = intesectsWall
					|| (path.intersection(wall.s) != null);
		}

		return !intesectsWall;
	}

	// public boolean isRobotParallelToWall() {
	// boolean aff[] = getRobotAffordances();
	//
	// // If I cannot move to any of the perpendicular directions I am near a
	// // wall
	// return !aff[GeomUtils.discretizeAction(-90)]
	// || !aff[GeomUtils.discretizeAction(90)];
	// }

	public void setWantedFeeder(int feeder, boolean wanted) {
		if (display)
			feederNodes.get(feeder).setWanted(wanted);
	}

	public void clearWantedFeeders() {
		for (FeederNode f : feederNodes)
			f.setWanted(false);
	}

	// @Override
	// public int getWantedFeeder() {
	// int wantedFeeder = -1;
	// for (int i = 0; i < feeders.size(); i++)
	// if (feeders.get(i).isWanted()) {
	// wantedFeeder = i;
	// break;
	// }
	//
	// return wantedFeeder;
	// }

	// public List<WallNode> getWalls() {
	// return wallNodes;
	// }

	public void dispose() {
		for (FeederNode f : feederNodes)
			f.terminate();
	}

	public boolean canRobotSeeFeeder(Integer fn, float halfFieldOfView,
			float visionDist) {
		float angleToFeeder = angleToFeeder(fn);
		boolean inField = angleToFeeder <= halfFieldOfView;
		// System.out.println(fn + " " + angleToFeeder);

		boolean intersects = false;
		Coordinate rPos = new Coordinate(getRobotPosition().x,
				getRobotPosition().y);
		Point3f fPosV = getFoodPosition(fn);
		Coordinate fPos = new Coordinate(fPosV.x, fPosV.y);
		LineSegment lineOfSight = new LineSegment(rPos, fPos);
		for (Wall w : getWalls())
			intersects = intersects || w.intersects(lineOfSight);

		boolean closeEnough = getRobotPosition().distance(
				new Point3f(getFoodPosition(fn))) < visionDist;

		return inField && !intersects && closeEnough;
	}

	/**
	 * Returns the absolute angle to the feeder
	 * 
	 * @param fn
	 * @return
	 */
	private float angleToFeeder(Integer fn) {
		return Math
				.abs(GeomUtils.angleToPointWithOrientation(
						getRobotOrientation(), getRobotPosition(),
						getFoodPosition(fn)));

	}

	// public boolean placeIntersectsWalls(Polygon c) {
	// boolean intersects = false;
	// for (WallNode w : wallNodes)
	// intersects = intersects || w.intersects(c);
	//
	// return intersects;
	// }

	public void setFlashingFeeder(int i, boolean flashing) {
		super.setFlashingFeeder(i, flashing);

		if (display)
			feederNodes.get(i).setFlashing(flashing);
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

	public List<Point3f> getVisibleWallEnds(float halfFieldOfView,
			float visionDist) {
		List<Point3f> openEnds = new LinkedList<Point3f>();
		List<Wall> innerWalls = new LinkedList<Wall>(getWalls());
		innerWalls.removeAll(initialWalls);
		for (Wall w : innerWalls) {
			Point3f p = new Point3f((float) w.s.p0.x, (float) w.s.p0.y, 0f);
			
			float minDist = Float.MAX_VALUE;
			for (Wall w2 : getWalls()){
				if (w2 != w){
					if (w2.distanceTo(p) < minDist)
						minDist = w2.distanceTo(p);
				}
			}
			if (minDist > OPEN_END_THRS)
				openEnds.add(p);

			p = new Point3f((float) w.s.p1.x, (float) w.s.p1.y, 0f);
			minDist = Float.MAX_VALUE;
			for (Wall w2 : getWalls()){
				if (w2 != w){
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

	private boolean pointCanBeSeenByRobot(Point3f p, float halfFieldOfView,
			float visionDist) {
		boolean inField = false, closeEnough = false, intersects = true;
		
		float angleToPoint = GeomUtils.angleToPointWithOrientation(
				getRobotOrientation(), getRobotPosition(), p);
		inField = Math.abs(angleToPoint) <= halfFieldOfView;

		if (inField) {
			closeEnough = getRobotPosition().distance(p) < visionDist;
			
			if (closeEnough){
				intersects = false;
				Coordinate rPos = new Coordinate(getRobotPosition().x,
						getRobotPosition().y);
				Coordinate fPos = new Coordinate(p.x, p.y);
				LineSegment lineOfSight = new LineSegment(rPos, fPos);
				for (Wall w : getWalls())
					intersects = intersects
							|| (w.s.p0.distance(fPos) != 0 && w.s.p1.distance(fPos) != 0  && w.intersects(lineOfSight));
			}
		}
		// System.out.println(inField + " " + !intersects + " " + closeEnough);
		return inField && !intersects && closeEnough;
	}

}
