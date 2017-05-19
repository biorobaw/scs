package edu.usf.experiment.utils;

import java.util.Arrays;
import java.util.Collections;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;

public class GeomUtils {

	public static String getCurrentDirectoryAbsolute() {
		return System.getProperty("user.dir");
	}

	public static void shuffleList(Object[] array) {
		Collections.shuffle(Arrays.asList(array));
	}
	
	
	///////////////////////////////////////////PABLO MODIFICATIONS//////////////////////////////////////////////////////////////////
	
	/**
	 * Returns relative angle to the base. Result is given in the range [-pi,pi)
	 * 
	 * @param angle
	 * @param base
	 * @return
	 */
	
	public static float relativeAngle(float angle, float base){
		float deltaAngle =(float) ((angle - base) % (2*Math.PI));
		if (deltaAngle >= Math.PI ) return (float)(deltaAngle - 2*Math.PI);
		if (deltaAngle < -Math.PI ) return (float)(deltaAngle + 2*Math.PI);
		return deltaAngle;
	
	}
	
	
	
	
	
	
	//////////////////////////////////////////END OF PABLO MODIFICATIONS////////////////////////////////////////////////////////////

	/**
	 * Returns a Quaternion representing the 3d rotation that transforms vector
	 * from into vector to
	 * 
	 * @param from
	 *            vector representing heading direction
	 * @param to
	 *            vector of the position of the desired goal
	 * @return
	 */
	public static Quat4f rotBetweenVectors(Vector3f from, Vector3f to) {
		// Taken from
		// http://lolengine.net/blog/2013/09/18/beautiful-maths-quaternion-from-vectors
		// from.normalize();
		// to.normalize();
		// Quat4f res = new Quat4f();
		// Vector3f cross = new Vector3f();
		// cross.cross(from, to);
		// cross.normalize();
		// float dot = from.dot(to);
		//
		// res.x = cross.x;
		// res.y = cross.y;
		// res.z = cross.z;
		// res.w = 1.f + dot;
		//
		// res.normalize();
		//
		// Taken
		// fromhttp://lolengine.net/blog/2014/02/24/quaternion-from-two-vectors-final
		from.normalize();
		to.normalize();
		float norm_u_norm_v = 1f;
		float real_part = norm_u_norm_v + from.dot(to);
		Vector3f w;
		if (real_part < 1.e-6f * norm_u_norm_v) {
			/*
			 * If u and v are exactly opposite, rotate 180 degrees around an
			 * arbitrary orthogonal axis. Axis normalisation can happen later,
			 * when we normalise the quaternion.
			 */
			real_part = (float) 0;
//			if (Math.abs(from.x) > Math.abs(from.z))
//				w = new Vector3f(-from.y, from.x, 0.f);
//			else
//				w = new Vector3f(0.f, -from.z, from.y);
			// We know rotations are always using Z axis
			w = new Vector3f(0,0,1);
		} else {
			/* Otherwise, build quaternion the standard way. */
			w = new Vector3f();
			w.cross(from, to);
		}

		Quat4f res = new Quat4f(w.x, w.y, w.z, real_part);
		res.normalize();
		return res;
	}

	// FIXME: should add one more inverse? result seems negated
	public static float angleToPointWithOrientation(Quat4f orientation,
			Point3f from, Point3f to) {
		Vector3f toPoint = pointsToVector(from, to);
		Quat4f rotTo = rotBetweenVectors(new Vector3f(1, 0, 0), toPoint);
		rotTo.inverse();
		rotTo.mul(orientation);
		return rotToAngle(rotTo);
	}
	
	// FIXME: should add one more inverse? result seems negated
	public static float angleToPointWithOrientation(float orientation, Point3f from, Point3f to) {
		return angleToPointWithOrientation(GeomUtils.angleToRot(orientation), from, to);
	}

	public static float angleDistance(float from, float to) {
		// Create complex numbers for both orientations
		double r1 = Math.cos(from);
		double i1 = Math.sin(from);
		double r2 = Math.cos(to);
		double i2 = Math.sin(to);
		// Conjugate from
		i1 = -i1;
		// Multiply them
		double r = r1 * r2 - i1 * i2;
		double i = i1 * r2 + r1 * i2;
		// Get the argument and complementary
		double arg = Math.atan2(i, r);
		double argComp;

		if (arg > 0)
			argComp = -(2 * Math.PI - arg);
		else
			argComp = 2 * Math.PI + arg;

		// Return the minimum of the absolute values
		return (float) Math.min(Math.abs(arg), Math.abs(argComp));
	}
	
	public static Vector3f pointsToVector(Point3f from, Point3f to) {
		Vector3f fVect = new Vector3f(to);
		fVect.sub(from);

		return fVect;
	}

	public static Quat4f angleToRot(float angle) {
		Quat4f res = new Quat4f();
		Transform3D t = new Transform3D();
		t.rotZ(angle);
		t.get(res);
		return res;
	}

	public static float rotToAngle(Quat4f rot) {
		rot.normalize();
		float angle = (float) (2 * Math.acos(rot.w)) * Math.signum(rot.z);
		// Get the shortest
		if (angle > Math.PI)
			angle -= Math.PI * 2;
		else if (angle < -Math.PI)
			angle -= -Math.PI * 2;

		return (float) (angle);
	}

	public static Quat4f angleToPoint(Point3f location) {
		Vector3f toPoint = new Vector3f(location);
		Quat4f rotTo = rotBetweenVectors(new Vector3f(1, 0, 0), toPoint);
		return rotTo;
	}
	
	/**
	 * Angle from rot1 to rot2
	 * @param rot1
	 * @param rot2
	 * @return
	 */
	public static float angleDiff(Quat4f rot1, Quat4f rot2){
		rot1.inverse();
		rot2.mul(rot1);
		return rotToAngle(rot2);
	}

	public static float angleDiff(float a1, float a2) {
		Quat4f rot1 = angleToRot(a1);
		Quat4f rot2 = angleToRot(a2);
		return angleDiff(rot1, rot2);
	}

	public static float distanceToPoint(Point3f p) {
		return (float) Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2) + Math.pow(p.z, 2));
	}

	public static float getFeederReward(Point3f position, float rotationAngle, float maxReward,
			Subject subject, LocalizableRobot robot) {
		Quat4f rotToFood = GeomUtils.angleToPoint(position);

		Quat4f actionAngle = GeomUtils.angleToRot(rotationAngle);

		float angleDiff = Math.abs(GeomUtils.angleDiff(actionAngle,
				rotToFood));

		float rotationSteps = angleDiff / subject.getMinAngle();

		float dist = GeomUtils.distanceToPoint(position);

		float forwardSteps = dist / subject.getStepLenght();

		// TODO: improve this function
		return (float) (maxReward * Math.exp(-(forwardSteps + rotationSteps) / 10));
	}

	/**
	 * Returns the steps needed to get to a feeder
	 * @param feederPos
	 * @param subject
	 * @return
	 */
	public static float getStepsToFeeder(Point3f feederPos, Subject subject) {
		Quat4f rotToFood = GeomUtils.angleToPoint(feederPos);

		float angleDiff = Math.abs(GeomUtils.rotToAngle(rotToFood)); 

		int rotationSteps = (int) Math.ceil(angleDiff / subject.getMinAngle());

		float dist = GeomUtils.distanceToPoint(feederPos);

		int forwardSteps = (int) Math.ceil(dist / subject.getStepLenght());

		return forwardSteps + rotationSteps;
	}
	
	/**
	 * Simulate the inverse movement of a feeder based on its position and affordance to execute
	 * @param position
	 * @param af
	 * @return
	 */
	public static Point3f simulate(Point3f position, Affordance af) {
		if (af instanceof ForwardAffordance) {
			ForwardAffordance fw = (ForwardAffordance) af;
			position.add(new Point3f(-fw.getDistance(), 0, 0));
			return position;
		} else if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			Quat4f rot = GeomUtils.angleToRot(-ta.getAngle());
			Transform3D t = new Transform3D();
			t.set(rot);
			t.transform(position);
			return position;
		} else if (af instanceof EatAffordance){
			return position;
		} else 
			throw new RuntimeException("Simulation of unknown affordance");
	}

}
