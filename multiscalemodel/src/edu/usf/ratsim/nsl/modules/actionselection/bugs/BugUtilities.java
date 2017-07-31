package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.support.SonarUtils;
import flanagan.analysis.Regression;

public class BugUtilities {

	// TODO: change deltaT to be more realistic in seconds, and fix constants
	public static final float DEFAULT_OBSTACLE_FOUND_THRS = .1f;
	public static final float CLOSE_THRS = .3f;

	private static final float PROP_ANG_WALL_CLOSE = 2f;
	private static final float PROP_LINEAR_WF = 0.125f;
	private static final float PROP_LINEAR_GS = 0.25f;
	private static final float PROP_ANGULAR_GS = 1f;

	private static final float WL_FW_TARGET = .1f;
	private static final float WL_RIGHT_TARGET = .05f;
	private static final float WF_MIN_FW_VEL = .05f;
	private static final float WF_ROT_VEL_OBS_FRONT = 1f;
	private static final float MAX_GS_PROP_DIST = 0.3f;
	private static final float MAX_ERR = .2f;

	private static final float BLIND_LINEAR = 0.05f;
	private static final float BLIND_ANGULAR = .6f;
	private static final float PROP_ANG_PARALLEL = 1.3f;
	private static final float PROP_LINEAR_PARALLEL_X = 0.075f;
	private static final float PROP_LINEAR_PARALLEL_Y = 0.09f;
	private static final float PROP_LINEAR_AWAY = .3f;
	private static final float TARGET_WALL_AWAY = .14f;

	private static final double MIN_GS_LINEAR_COMP = 0.05f;
	private static final float PLANE_MEASURE_THRS = .2f;


	public static Velocities goalSeek(Coordinate rPos, float rOrient, Coordinate platPos) {
		float linear, angular;
		linear = (float) Math.max(MIN_GS_LINEAR_COMP,
				PROP_LINEAR_GS * Math.min(platPos.distance(rPos), MAX_GS_PROP_DIST));
		angular = PROP_ANGULAR_GS * GeomUtils.relativeAngleToPoint(rPos, rOrient, platPos);
		return new Velocities(linear, 0, angular);
	}

	public static Velocities wallFollowRight(Float1dPort readings, Float1dPort angles, float robotRadius,
			float obstFoundThrs) {
		float x, y, angular;

		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI / 6));
		float minLeft = SonarUtils.getMinReading(readings, angles, (float) (Math.PI / 2), (float) (Math.PI / 6));
		float leftFront = SonarUtils.getReading((float) (Math.PI / 6), readings, angles);
		// Get the entier front half to detect walls
		Map<Float, Float> wallReadings = SonarUtils.getReadings(readings, angles, 0f, (float) Math.PI);
		// Get measures below close thrs
		Map<Float, Float> planeMeasures = new HashMap<Float, Float>();
		for (Float angle : wallReadings.keySet()) {
			float reading = wallReadings.get(angle);
			if (reading < PLANE_MEASURE_THRS)
				planeMeasures.put(angle, reading);
		}

		// If no measures, just circle hoping to find the wall
		// If I see a wall with more wall going forward (leftFront sensor detecting), also should just circle it
		if (planeMeasures.size() <= 1) {
			x = BLIND_LINEAR;
			y = BLIND_LINEAR/2;
			angular = BLIND_ANGULAR;
		} else  {
			double planeAngle;
			float distToPlane = 0;
			if (planeMeasures.size() > 1) {
				double[] xs = new double[planeMeasures.size() * 2];
				double[] ys = new double[planeMeasures.size() * 2];
				int i = 0;
				for (Float angle : planeMeasures.keySet()) {
					xs[i] = (robotRadius + planeMeasures.get(angle)) * Math.cos(angle);
					ys[i] = (robotRadius + planeMeasures.get(angle)) * Math.sin(angle);
					xs[i + 1] = xs[i];
					ys[i + 1] = ys[i];
					i += 2;
				}
				Regression reg = new Regression(xs, ys);
				reg.linear();
				double[] estimates = reg.getBestEstimates();
				double m = estimates[1];
				double n = estimates[0];
				// If it intersects y in the negative side, the line is to the right, so following the opposite direction
				if (n < 0)
					planeAngle = Math.atan(m) - Math.PI;
				else
					planeAngle = Math.atan(m);
				
				
				LineSegment planeSeg;
				if (m != 0)
					planeSeg = new LineSegment(new Coordinate(0, n), new Coordinate(-(n/m), 0));
				else
					planeSeg = new LineSegment(new Coordinate(0, n), new Coordinate(1, n));
				distToPlane = (float) planeSeg.distancePerpendicular(new Coordinate());
			} else {
				planeAngle = 0;
				for (Float angle : planeMeasures.keySet()){
					planeAngle = GeomUtils.standardAngle(angle) - Math.PI / 2;
					distToPlane = planeMeasures.get(angle);
				}
			}
			
			
			float awayErr = TARGET_WALL_AWAY - distToPlane;
			float parallelAngle = (float) planeAngle;
			float awayAngle = (float) (planeAngle - Math.PI / 2);
			
			
			x = 0; y = 0; angular = 0;
//			if (Math.abs(parallelAngle) < Math.PI / 4){
				x = (float) (Math.cos(parallelAngle) * PROP_LINEAR_PARALLEL_X + Math.cos(awayAngle) * awayErr * PROP_LINEAR_AWAY); 
				y= (float) (Math.sin(parallelAngle) * PROP_LINEAR_PARALLEL_Y + Math.sin(awayAngle) * awayErr * PROP_LINEAR_AWAY); 
//			}
			angular = (float) (PROP_ANG_PARALLEL * (parallelAngle));
			System.out.println(parallelAngle);
		}

		return new Velocities(x, y, angular);
	}

	public static Velocities wallFollowLeft(Float1dPort readings, Float1dPort angles) {
		float linear, angular;

		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI / 3));
		float minRight = SonarUtils.getMinReading(readings, angles, (float) (-Math.PI / 2), (float) (Math.PI / 6));
		float front = SonarUtils.getReading(0f, readings, angles);
		float right = SonarUtils.getReading((float) (-Math.PI / 2), readings, angles);
		float rightFront = SonarUtils.getReading((float) (-Math.PI / 4), readings, angles);
		if (minFront < DEFAULT_OBSTACLE_FOUND_THRS || minRight >= 1.5 * DEFAULT_OBSTACLE_FOUND_THRS) {
			angular = WF_ROT_VEL_OBS_FRONT;
			linear = 0;
		} else {
			// Get the current relation and the target relation (wall parallel
			// to robot)
			float quot = right / rightFront;
			float targetquot = (float) Math.cos(Math.PI / 8);

			float close_prop = right - WL_RIGHT_TARGET;

			angular = -PROP_ANG_PARALLEL * (targetquot - quot) - PROP_ANG_WALL_CLOSE * close_prop;

			linear = Math.max(PROP_LINEAR_WF * (front - WL_FW_TARGET), WF_MIN_FW_VEL);
		}

		return new Velocities(linear, 0, angular);
	}
}
