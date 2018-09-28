package edu.usf.experiment.utils;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.util.NoninvertibleTransformationException;

import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.platform.Platform;
import edu.usf.experiment.universe.wall.Wall;

public class GeomUtils {
	
	public static float angleToPoint(Coordinate p) {
		return (float) Math.atan2(p.y, p.x);
	}
	
	public static float distanceToPoint(Coordinate p) {
		return (float) Math.sqrt(Math.pow(p.x, 2) + Math.pow(p.y, 2));
	}

	/**
	 * Returns relative angle to the base. Result is given in the range [-pi,pi)
	 * 
	 * @param angle
	 * @param base
	 * @return
	 */
	public static float relativeAngle(float angle, float base){
		float deltaAngle =(float) ((angle - base) % (2*Math.PI));
		
		return standardAngle(deltaAngle);
	
	}
	
	public static Coordinate relativeCoords(Coordinate p, Coordinate t, float theta) {
		RigidTransformation rT = new RigidTransformation(t, theta);
		Coordinate relP = new Coordinate();
		try {
			rT.getInverse().transform(p, relP);
		} catch (NoninvertibleTransformationException e) {
			e.printStackTrace();
		}
		return relP;
	}
	
	public static float relativeAngleToPoint(Coordinate t, float theta, Coordinate p){
		Coordinate relP = relativeCoords(p, t, theta);
		return angleToPoint(relP);
	}

	/**
	 * Returns the steps needed to get to a feeder
	 * @param feederPos
	 * @param subject
	 * @return
	 */
	public static float getStepsToFeeder(Coordinate feederPos, float minAngle, float stepLength) {
		float rotToFood = angleToPoint(feederPos);

		float angleDiff = Math.abs(rotToFood); 

		int rotationSteps = (int) Math.ceil(angleDiff / minAngle);

		float dist = GeomUtils.distanceToPoint(feederPos);

		int forwardSteps = (int) Math.ceil(dist / stepLength);

		return forwardSteps + rotationSteps;
	}
	
	/**
	 * Simulate the inverse movement of a feeder based on its position and affordance to execute
	 * @param position
	 * @param af
	 * @return
	 */
	public static Coordinate simulate(Coordinate position, Affordance af) {
		if (af instanceof ForwardAffordance) {
			ForwardAffordance fw = (ForwardAffordance) af;
			RigidTransformation step = new RigidTransformation(new Coordinate(-fw.getDistance(), 0), 0);
			Coordinate nextP = new Coordinate();
			step.transform(position, nextP);
			return nextP;
		} else if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			RigidTransformation rot = new RigidTransformation(new Coordinate(),-ta.getAngle());
			Coordinate nextP = new Coordinate();
			rot.transform(position, nextP);
			return nextP;
		} else if (af instanceof EatAffordance){
			return position;
		} else 
			throw new RuntimeException("Simulation of unknown affordance");
	}

	public static Rectangle2D.Float computeBoundingRect(Collection<Feeder> feeders, Collection<Wall> walls, Collection<Platform> platforms) {
		float minx = Float.MAX_VALUE, miny = Float.MAX_VALUE;
		float maxx = -Float.MAX_VALUE, maxy = -Float.MAX_VALUE;
		
		List<Coordinate> coords = new LinkedList<Coordinate>();
		
		for (Feeder f : feeders)
			coords.add(new Coordinate(f.getPosition().x, f.getPosition().y));
		for (Wall w : walls){
			coords.add(w.s.p0);
			coords.add(w.s.p1);
		}
		for (Platform p : platforms)
			coords.add(new Coordinate(p.getPosition().x, p.getPosition().y));

		for (Coordinate c : coords){
			if (c.x > maxx)	maxx = (float) c.x;
			if (c.y > maxy)	maxy = (float) c.y;
			if (c.x < minx)	minx = (float) c.x;
			if (c.y < miny)	miny = (float) c.y;
		}
		
		return  new Rectangle2D.Float(minx, miny, maxx - minx, maxy - miny);
		
	}
	
	public static void main(String[] args){
		
		System.out.println(angleBetweenAngles((float)(3*Math.PI/4),(float) (-3*Math.PI/4), (float)(Math.PI/8)));
	}

	/**
	 * Decides whether an angle is inside the shortest interval defined by two angles 
	 * @param angle
	 * @param angleToP1
	 * @param angleToP2
	 * @return
	 */
	public static boolean angleBetweenAngles(float angle, float a1, float a2) {
		float a1a2 = Math.abs(GeomUtils.relativeAngle(a1, a2));
		float a1angle = Math.abs(GeomUtils.relativeAngle(a1, angle));
		float a2angle = Math.abs(GeomUtils.relativeAngle(a2, angle));
		return a1angle <= a1a2 && a2angle <= a1a2;
	}

	public static float standardAngle(Float angle) {
		float norm = angle;
		if (norm >= Math.PI ) return (float)(norm - 2*Math.PI);
		if (norm < -Math.PI ) return (float)(norm + 2*Math.PI);
		return angle;
	}

}
