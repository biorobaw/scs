package edu.usf.ratsim.robot.virtual;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import edu.usf.experiment.robot.Landmark;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.SonarRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.ratsim.experiment.subject.NotImplementedException;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

public class VirtualRobot extends LocalizableRobot implements SonarRobot {

	private static final float ROBOT_LENGTH = .1f;

	public VirtUniverse universe;

	private Random r;

	private float noise;

	private float lookaheadSteps;

	private float visionDist;

	private float halfFieldOfView;

	private float closeThrs;

	private float translationRotationNoise;

	private boolean closestFeederValid;

	private Feeder previouslyFoundFeeder;

	private int lastAteFeeder;

	private int lastTriedToEat;

	private List<Float> sonarAngles;

	private float sonarAperture;

	private float sonarMaxDist;

	private int sonarNumRays;

	public void setCloseThreshold(float value) {
		this.closeThrs = value;
	}

	public VirtualRobot(ElementWrapper params, Universe u) {
		super(params);

		noise = params.getChildFloat("noise");
		translationRotationNoise = params.getChildFloat("translationRotationNoise");
		lookaheadSteps = params.getChildFloat("lookaheadSteps");
		halfFieldOfView = params.getChildFloat("halfFieldOfView");
		visionDist = params.getChildFloat("visionDist");
		closeThrs = params.getChildFloat("closeThrs");

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

		r = RandomSingleton.getInstance();

		closestFeederValid = false;
		lastAteFeeder = -1;
		lastTriedToEat = -1;
	}

	public void rotate(float grados) {
		universe.rotateRobot(grados + noise * r.nextFloat() * grados);
		closestFeederValid = false;
	}

	public void startRobot() {
	}

	public boolean hasFoundFood() {
		return universe.hasRobotFoundFood();
	}

	public void forward(float dist) {
		universe.moveRobot(new Vector3f(dist + dist * r.nextFloat() * noise, 0f, 0f));
		universe.rotateRobot((2 * r.nextFloat() - 1) * translationRotationNoise);

		closestFeederValid = false;
	}

	@Override
	public void eat() {
		if (Debug.printRobotAte)
			System.out.println("Robot ate");
		universe.robotEat();
	}

	public List<Landmark> getLandmarks() {
		return getLandmarks(-1);
	}

	public List<Landmark> getLandmarks(int except) {
		List<Landmark> res = new LinkedList<Landmark>();
		for (Integer i : universe.getFeederNums())
			if (i != except)
				if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
					// Get relative position
					Point3f fPos = universe.getFoodPosition(i);
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

	public List<Feeder> getVisibleFeeders(int[] except) {
		List<Feeder> res = new LinkedList<Feeder>();
		for (Integer i : universe.getFeederNums())
			if (except == null || !in(i, except))
				if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist)) {
					// Get relative position
					Point3f relFPos = getRelativePos(universe.getFoodPosition(i));
					// Return the landmark
					Feeder relFeeder = new Feeder(universe.getFeeder(i));
					relFeeder.setPosition(relFPos);
					res.add(relFeeder);
				}

		return res;
	}

	private boolean in(int o, int[] except) {
		for (int i = 0; i < except.length; i++)
			if (except[i] == o)
				return true;
		return false;
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
		for (Integer i : universe.getFeederNums())
			if (universe.canRobotSeeFeeder(i, halfFieldOfView, visionDist) && universe.isFeederFlashing(i)) {
				// Get relative position
				Point3f fPos = universe.getFoodPosition(i);
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

	// @Override
	// public Feeder getClosestFeeder(int lastFeeder) {
	// // If feeder was invalidated - re compute it
	// // if (!closestFeederValid){
	// // previouslyFoundFeeder = getClosestFeederImpl(-1);
	// // closestFeederValid = true;
	// // }
	// //
	// // // No feeder visible at all
	// // if (previouslyFoundFeeder == null)
	// // return null;
	// //
	// // // Optimize to return previously computed if hasnt moved
	// // if (previouslyFoundFeeder.getId() == lastFeeder){
	// return getClosestFeederImpl(lastFeeder);
	// // } else
	// // return previouslyFoundFeeder;
	//
	// }

	private Feeder getClosestFeederImpl(int lastFeeder) {
		int[] except = { lastFeeder };
		List<Feeder> feeders = getVisibleFeeders(except);

		if (feeders.isEmpty())
			return null;

		Feeder closest = feeders.get(0);
		Point3f zero = new Point3f(0, 0, 0);
		// System.out.println("Searching for closest feeder");
		for (Feeder feeder : feeders) {
			// System.out.println("Feeder " + feeder.getId() + " at dist "
			// + feeder.getPosition().distance(zero));

			if (feeder.getPosition().distance(zero) < closest.getPosition().distance(zero))
				closest = feeder;
		}
		// System.out.println("Closest " + closest.getId());

		return closest;
	}

	@Override
	public boolean isFeederClose() {
		Feeder f = getClosestFeeder();
		return f != null && f.getPosition().distance(new Point3f()) < closeThrs;
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
	public List<Affordance> checkAffordances(List<Affordance> affs) {
		for (Affordance af : affs) {
			boolean realizable;
			if (af instanceof TurnAffordance) {
				TurnAffordance ta = (TurnAffordance) af;
				// Either it can move there, or it cannot move forward and the
				// other angle is not an option
				realizable = !universe.canRobotMove(0, ROBOT_LENGTH * lookaheadSteps)
						// && !canRobotMove(-ta.getAngle(), ROBOT_LENGTH))
						|| universe.canRobotMove(ta.getAngle(), ROBOT_LENGTH * lookaheadSteps);
				// realizable = true;
			} else if (af instanceof ForwardAffordance)
				realizable = universe.canRobotMove(0, ROBOT_LENGTH * lookaheadSteps);
			else if (af instanceof EatAffordance) {
				// realizable = hasRobotFoundFood();
				if (getClosestFeeder() != null)
					realizable = getClosestFeeder().getPosition().distance(new Point3f()) < closeThrs;
				else
					realizable = false;
			} else
				throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");

			af.setRealizable(realizable);
		}

		return affs;
	}

	@Override
	public boolean checkAffordance(Affordance af) {
		boolean realizable;
		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			// Either it can move there, or it cannot move forward and the
			// other angle is not an option
			realizable = !universe.canRobotMove(0, ROBOT_LENGTH * lookaheadSteps)
					// && !canRobotMove(-ta.getAngle(), ROBOT_LENGTH))
					|| universe.canRobotMove(ta.getAngle(), ROBOT_LENGTH * lookaheadSteps);
			// realizable = true;
		} else if (af instanceof ForwardAffordance)
			realizable = universe.canRobotMove(0, ROBOT_LENGTH * lookaheadSteps);
		else if (af instanceof EatAffordance) {
			// realizable = hasRobotFoundFood();
			if (getClosestFeeder() != null)
				realizable = getClosestFeeder().getPosition().distance(new Point3f()) < closeThrs;
			else
				realizable = false;
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");

		af.setRealizable(realizable);
		return realizable;

	}

	@Override
	public void executeAffordance(Affordance af, Subject sub) {
		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			List<Affordance> forward = new LinkedList<Affordance>();
			forward.add(new ForwardAffordance(ta.getDistance()));
			rotate(ta.getAngle());

		} else if (af instanceof ForwardAffordance)
			forward(((ForwardAffordance) af).getDistance());
		else if (af instanceof EatAffordance) {
			// Updates food in universe
			sub.setTriedToEat();
			if (isFeederClose() && getClosestFeeder().hasFood()) {
				eat();
				sub.setHasEaten(true);
				lastAteFeeder = getClosestFeeder().getId();
				if (Debug.printTryingToEat)
					System.out.println("Ate from a feeder with food");

			} else {
				if (Debug.printTryingToEat)
					System.out.println("Trying to eat from empty feeder");
			}
			lastTriedToEat = getClosestFeeder().getId();
			// rotate((float) Math.PI);
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");
	}

	@Override
	public boolean seesFeeder() {
		return getClosestFeeder() != null;
	}

	@Override
	public List<Feeder> getAllFeeders() {
		return universe.getFeeders();
	}

	@Override
	public float getDistanceToClosestWall() {
		return universe.getDistanceToClosestWall();
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
		return universe.isWallCloseToSides(ROBOT_LENGTH / 2, distToConsider);
	}

	@Override
	public Feeder getFeederInFront() {
		int except[] = {};
		List<Feeder> visFeeders = getVisibleFeeders(except);
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
		return universe.hasRobotFoundPlatform();
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

}
