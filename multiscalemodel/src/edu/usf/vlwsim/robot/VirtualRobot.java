package edu.usf.vlwsim.robot;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import com.sun.corba.se.impl.ior.GenericTaggedProfile;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Landmark;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverseUtilities;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.universe.platform.PlatformUniverseUtilities;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.ratsim.support.NotImplementedException;
import edu.usf.vlwsim.universe.VirtUniverse;

public class VirtualRobot implements FeederRobot, LocalizableRobot, SonarRobot, PlatformRobot,
		WallRobot, TeleportRobot {

	private final float ROBOT_LENGTH = .1f;

	private VirtUniverse universe;


	private float visionDist;

	private float halfFieldOfView;

	private float closeThrs;

	private boolean closestFeederValid;

	private Feeder previouslyFoundFeeder;

	private int lastAteFeeder;

	private int lastTriedToEat;

	private List<Float> sonarAngles;

	private float sonarAperture;

	private float sonarMaxDist;

	private int sonarNumRays;

	private boolean lastSuccessfulEat;
	private boolean lastAte;

	private float robotHeading;

	public VirtualRobot(ElementWrapper params, Universe u) {
		
		

		halfFieldOfView = params.getChildFloat("halfFieldOfView");
		visionDist = params.getChildFloat("visionDist");
		setCloseThrs(params.getChildFloat("closeThrs"));
		

		// Sonar configuration
		if (params.getChild("sonarAngles") != null) {
			sonarAngles = params.getChildFloatList("sonarAngles");
			if (!sonarAngles.isEmpty()) {
				sonarAperture = params.getChildFloat("sonarAperture");
				sonarMaxDist = params.getChildFloat("sonarMaxDist");
				sonarNumRays = params.getChildInt("sonarNumRays");
			}
		}

		universe = (VirtUniverse) u;
		if (universe == null)
			throw new RuntimeException("A virtual universe must be created" + " before Virtual Robot is created");

		

		closestFeederValid = false;
		lastAteFeeder = -1;
		lastTriedToEat = -1;
		lastAte = false;
		lastSuccessfulEat = false;
		
	}

	

	public void startRobot() {
	}

	public boolean hasFoundFood() {
		return FeederUniverseUtilities.hasRobotFoundFood(universe.getFeeders(), universe.getRobotPosition(), universe.getCloseThrs());
	}

	

	@Override
	public void eat() {
		if (Debug.printRobotAte)
			System.out.println("Robot trying to eat");
		lastAte = true;
		lastTriedToEat = FeederUtils.getClosestFeeder(getVisibleFeeders()).getId();
		lastSuccessfulEat = false;
		universe.setRobotEat();
	}

	public List<Landmark> getLandmarks() {
		return getLandmarks(-1);
	}

	public List<Landmark> getLandmarks(int except) {
		List<Landmark> res = new LinkedList<Landmark>();
		for (Integer i : FeederUniverseUtilities.getFeederNums(universe.getFeeders()))
			if (i != except)
				if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
					// Get relative position
					Point3f fPos = universe.getFeeder(i).getPosition();
					Point3f rPos = universe.getRobotPosition();
					Point3f relFPos = new Point3f(GeomUtils.pointsToVector(rPos, fPos));
					// Rotate to robots framework
					Quat4f rRot = universe.getRobotOrientation();
					rRot.inverse();
					Transform3D t = new Transform3D();
					t.setRotation(rRot);
					t.transform(relFPos);
					// Return the landmark
					res.add(new Landmark(i, relFPos));
				}

		return res;
	}

	public List<Feeder> getVisibleFeeders() {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Integer i : FeederUniverseUtilities.getFeederNums(universe.getFeeders()))
			if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
				// Get relative position
				Point3f relFPos = getRelativePos(universe.getFeeder(i).getPosition());
				// Return the landmark
				Feeder relFeeder = new Feeder(universe.getFeeder(i));
				relFeeder.setPosition(relFPos);
				res.add(relFeeder);
			}

		return res;
	}

	private Point3f getRelativePos(Point3f fPos) {
		Point3f rPos = universe.getRobotPosition();
		Point3f relFPos = new Point3f(GeomUtils.pointsToVector(rPos, fPos));
		// Rotate to robots framework
		Quat4f rRot = universe.getRobotOrientation();
		rRot.inverse();
		Transform3D t = new Transform3D();
		t.setRotation(rRot);
		t.transform(relFPos);
		return relFPos;
	}

	@Override
	public Feeder getFlashingFeeder() {
		for (Integer i : FeederUniverseUtilities.getFeederNums(universe.getFeeders()))
			if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist) && universe.getFeeder(i).isFlashing()) {
				// Get relative position
				Point3f fPos = universe.getFeeder(i).getPosition();
				Point3f rPos = universe.getRobotPosition();
				Point3f relFPos = new Point3f(GeomUtils.pointsToVector(rPos, fPos));
				// Rotate to robots framework
				Quat4f rRot = universe.getRobotOrientation();
				rRot.inverse();
				Transform3D t = new Transform3D();
				t.setRotation(rRot);
				t.transform(relFPos);
				Feeder relFeeder = new Feeder(universe.getFeeder(i));
				relFeeder.setPosition(relFPos);
				return relFeeder;
			}
		return null;
	}

	@Override
	public boolean seesFlashingFeeder() {
		// if(getFlashingFeeder() != null)
		// System.out.println("Seeing flashing feeder");
		return getFlashingFeeder() != null;
	}

	@Override
	public boolean isFeederClose() {
		Feeder f = FeederUtils.getClosestFeeder(getVisibleFeeders());
		return f != null && f.getPosition().distance(new Point3f()) < getCloseThrs();
	}

	@Override
	public Point3f getPosition() {
		return universe.getRobotPosition();
	}

	@Override
	public float getOrientationAngle() {
		return universe.getRobotOrientationAngle();
	}

	// @Override
	// public Quat4f getOrientation() {
	// return universe.getRobotOrientation();
	// }

	

	@Override
	public boolean seesFeeder() {
		return FeederUtils.getClosestFeeder(getVisibleFeeders()) != null;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		return universe.getFeeders();
	}

	@Override
	public float getDistanceToClosestWall() {
		return WallUniverseUtilities.getDistanceToClosestWall(universe.getWalls(), universe.getRobotPosition());
	}

	@Override
	public List<Point3f> getVisibleWallEnds() {
		List<Point3f> absoluteWEnds = universe.getVisibleWallEnds(halfFieldOfView, visionDist);
		List<Point3f> relativeWEnds = new LinkedList<Point3f>();
		for (Point3f p : absoluteWEnds) {
			relativeWEnds.add(getRelativePos(p));
		}
		return relativeWEnds;
	}

	@Override
	public float getHalfFieldView() {
		return halfFieldOfView;
	}

	@Override
	public int closeToNoseWalls(float distToConsider) {
		return universe.isWallCloseToSides(getRobotLength() / 2, distToConsider);
	}

	@Override
	public Feeder getFeederInFront() {
		List<Feeder> visFeeders = getVisibleFeeders();
		float angle = Float.MAX_VALUE;
		Feeder feederInFront = null;
		for (Feeder f : visFeeders) {
			float angleToFeeder = GeomUtils.rotToAngle(GeomUtils.angleToPoint(f.getPosition()));
			if (Math.abs(angleToFeeder) < Math.abs(angle)) {
				angle = angleToFeeder;
				feederInFront = f;
			}
		}

		return feederInFront;
	}

	@Override
	public int getLastAteFeeder() {
		return lastAteFeeder;
	}

	@Override
	public int getLastTriedToEatFeeder() {
		return lastTriedToEat;
	}

	@Override
	public boolean hasFoundPlatform() {
		return PlatformUniverseUtilities.hasRobotFoundPlatform(universe.getPlatforms(), universe.getRobotPosition());
	}

	public void moveContinous(float lVel, float angVel) {
		throw new NotImplementedException();
	}

	@Override
	public float[] getSonarReadings() {
		float[] readings = new float[sonarAngles.size()];

		int i = 0;
		for (float angle : sonarAngles)
			readings[i++] = universe.getRobotSonarReading(angle, sonarAperture, sonarMaxDist, sonarNumRays);

		return readings;
	}

	@Override
	public float[] getSonarAngles() {
		float[] angles = new float[sonarAngles.size()];
		int i = 0;
		for (float angle : sonarAngles)
			angles[i++] = angle;
		return angles;
	}

	@Override
	public float getSonarAperture() {
		return sonarAperture;
	}

	@Override
	public boolean hasRobotEaten() {
		return lastAte && lastSuccessfulEat;
	}

	@Override
	public boolean hasRobotTriedToEat() {
		return lastAte;
	}

	@Override
	public void clearEaten() {
		lastAte = false;
		lastSuccessfulEat = false;
	}

	@Override
	public void setAte() {
		lastSuccessfulEat = true;
	}

	public void moved() {
		closestFeederValid = false;
		lastAte = false;		
	}

	public float getCloseThrs() {
		return closeThrs;
	}

	public void setCloseThrs(float closeThrs) {
		this.closeThrs = closeThrs;
	}

	public float getRobotLength() {
		return ROBOT_LENGTH;
	}

	@Override
	public void setPosition(Point3f pos) {
		universe.setRobotPosition(new Point2D.Float(pos.x, pos.y), 0f);
	}

}
