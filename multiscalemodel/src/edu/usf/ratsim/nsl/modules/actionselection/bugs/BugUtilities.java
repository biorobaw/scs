package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.support.SonarUtils;
import flanagan.analysis.Regression;

public class BugUtilities {

	// TODO: change deltaT to be more realistic in seconds, and fix constants
	public static final float OBSTACLE_FOUND_THRS = .1f;
	public static final float CLOSE_THRS = .1f;


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
	private static final float PLANE_ESTIMATION_THRS = 0.25f;
	private static final float PROP_ANG_PARALLEL =1.5f;
	private static final float TARGET_WALL_AWAY = .05f;
	private static final float PROP_ANG_AWAY = 1;

	public static Velocities goalSeek(Coordinate rPos, float rOrient, Coordinate platPos) {
		float linear, angular;
		linear = (float) (PROP_LINEAR_GS * Math.min(platPos.distance(rPos), MAX_GS_PROP_DIST));
		angular = PROP_ANGULAR_GS * GeomUtils.relativeAngleToPoint(rPos, rOrient, platPos);
		return new Velocities(linear, 0, angular);
	}

	public static Velocities wallFollowRight(Float1dPort readings, Float1dPort angles, float robotRadius) {
		float x, y, angular;

		// float minLeft = SonarUtils.getMinReading(readings, angles, (float)
		// (Math.PI/2), (float) (Math.PI/12));
		// float front = SonarUtils.getReading(0f, readings, angles);
		// float left = SonarUtils.getReading((float) (Math.PI/2), readings,
		// angles);
		// float leftFront = SonarUtils.getReading((float) (Math.PI/3),
		// readings, angles);
		// float leftBack = SonarUtils.getReading((float) (2*Math.PI/3),
		// readings, angles);
		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI / 6));
		if (minFront < OBSTACLE_FOUND_THRS) {
			x = 0; y = 0;
			angular = -BLIND_ANGULAR;
		} else {

			Map<Float, Float> leftReadings = SonarUtils.getReadings(readings, angles, (float) (Math.PI / 2),
					(float) (Math.PI / 6));
			// Get measures below close thrs
			Map<Float, Float> planeMeasures = new HashMap<Float, Float>();
			for (Float angle : leftReadings.keySet()) {
				float reading = leftReadings.get(angle);
				if (reading < PLANE_ESTIMATION_THRS)
					planeMeasures.put(angle, reading);
			}

			if (planeMeasures.isEmpty()) {
				x = BLIND_LINEAR;
				y = 0;
				angular = BLIND_ANGULAR;
			} else {
				double planeAngle;
				if (planeMeasures.size() > 1) {
					double[] xs = new double[planeMeasures.size() * 2];
					double[] ys = new double[planeMeasures.size() * 2];
					int i = 0;
					for (Float angle : planeMeasures.keySet()) {
						xs[i] = (robotRadius + planeMeasures.get(angle)) * Math.cos(angle);
						ys[i] = (robotRadius + planeMeasures.get(angle)) * Math.sin(angle);
						xs[i+1] = xs[i];
						ys[i+1] = ys[i];
						i += 2;
					}
					Regression reg = new Regression(xs, ys);
					reg.linear();
					double[] estimates = reg.getBestEstimates();
					double m = estimates[1];
					planeAngle = Math.atan(m);
				} else {
					planeAngle = 0;
					for (Float angle : planeMeasures.keySet())
						planeAngle = angle - Math.PI / 2;
				}

				float minLeft = SonarUtils.getMinReading(readings, angles, (float) (Math.PI / 2),
						(float) (Math.PI / 6));
				float awayErr = TARGET_WALL_AWAY - minLeft;
				x = BLIND_LINEAR;
				y = -PROP_ANG_AWAY * awayErr;
				angular = (float) (PROP_ANG_PARALLEL * planeAngle );
			}
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
		if (minFront < OBSTACLE_FOUND_THRS || minRight >= 1.5 * OBSTACLE_FOUND_THRS) {
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
