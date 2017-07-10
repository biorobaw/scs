package edu.usf.vlwsim.robot;

import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.FeederRobot;
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

public class VirtualRobot
		implements FeederRobot, LocalizableRobot, SonarRobot, PlatformRobot, WallRobot, TeleportRobot {

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
		return FeederUniverseUtilities.hasRobotFoundFood(universe.getFeeders(), universe.getRobotPosition(),
				universe.getCloseThrs());
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

	public List<Feeder> getVisibleFeeders() {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Integer i : FeederUniverseUtilities.getFeederNums(universe.getFeeders()))
			if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
				// Get relative position
				Coordinate relFPos = GeomUtils.relativeCoords(universe.getFeeder(i).getPosition(),
						universe.getRobotPosition(), universe.getRobotOrientationAngle());
				// Return the landmark
				Feeder relFeeder = new Feeder(universe.getFeeder(i));
				relFeeder.setPosition(relFPos);
				res.add(relFeeder);
			}

		return res;
	}

	@Override
	public Feeder getFlashingFeeder() {
		for (Integer i : FeederUniverseUtilities.getFeederNums(universe.getFeeders()))
			if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist) && universe.getFeeder(i).isFlashing()) {
				// Get relative position
				Coordinate fPos = universe.getFeeder(i).getPosition();
				Coordinate rPos = universe.getRobotPosition();
				float rOrient = universe.getRobotOrientationAngle();

				Coordinate relFPos = GeomUtils.relativeCoords(fPos, rPos, rOrient);
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
		return f != null && f.getPosition().distance(new Coordinate()) < getCloseThrs();
	}

	@Override
	public Coordinate getPosition() {
		return universe.getRobotPosition();
	}

	@Override
	public float getOrientationAngle() {
		return universe.getRobotOrientationAngle();
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
	public List<Coordinate> getVisibleWallEnds() {
		List<Coordinate> absoluteWEnds = universe.getVisibleWallEnds(halfFieldOfView, visionDist);
		List<Coordinate> relativeWEnds = new LinkedList<Coordinate>();
		for (Coordinate p : absoluteWEnds) {
			relativeWEnds
					.add(GeomUtils.relativeCoords(p, universe.getRobotPosition(), universe.getRobotOrientationAngle()));
		}
		return relativeWEnds;
	}

	@Override
	public float getHalfFieldView() {
		return halfFieldOfView;
	}

	@Override
	public Feeder getFeederInFront() {
		List<Feeder> visFeeders = getVisibleFeeders();
		float angle = Float.MAX_VALUE;
		Feeder feederInFront = null;
		for (Feeder f : visFeeders) {
			float angleToFeeder = GeomUtils.angleToPoint(f.getPosition());
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
		for (float angle : sonarAngles) {
			readings[i++] = universe.getRobotSonarReading(angle, sonarAperture, sonarMaxDist, sonarNumRays);
		}

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
	public float getSonarMaxReading() {
		return sonarMaxDist;
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
	public void setPosition(Coordinate pos) {
		universe.setRobotPosition(new Coordinate(pos.x, pos.y));
	}

}
