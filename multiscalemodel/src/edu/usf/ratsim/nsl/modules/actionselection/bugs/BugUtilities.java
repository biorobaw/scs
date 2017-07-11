package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.support.SonarUtils;

public class BugUtilities {
	
	//TODO: change deltaT to be more realistic in seconds, and fix constants 
	public static final float OBSTACLE_FOUND_THRS = .15f;
	public static final float CLOSE_THRS = .15f;
	
	private static final float PROP_ANG_PARALLEL = 1f;
	private static final float PROP_ANG_WALL_CLOSE = 2f;
	private static final float PROP_LINEAR_WF = 0.125f;
	private static final float PROP_LINEAR_GS = 0.25f;
	private static final float PROP_ANGULAR_GS = 100f;

	private static final float WL_FW_TARGET = .15f;   	
	private static final float WL_RIGHT_TARGET = .1f; 
	private static final float WF_MIN_FW_VEL = .05f;
	private static final float WF_ROT_VEL_OBS_FRONT = 1f;
	private static final float MAX_GS_PROP_DIST = 0.3f;
	private static final float MAX_ERR = .2f;

	public static Velocities goalSeek(Coordinate rPos, float rOrient, Coordinate platPos) {
		float linear, angular; 
		linear = (float) (PROP_LINEAR_GS * Math.min(platPos.distance(rPos), MAX_GS_PROP_DIST));
		angular = PROP_ANGULAR_GS * GeomUtils.relativeAngleToPoint(rPos, rOrient, platPos);
		return new Velocities(linear, angular);
	}

	public static Velocities wallFollowRight(Float1dPort readings, Float1dPort angles) {
		float linear, angular; 
		
		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI/3));
		float minLeft = SonarUtils.getMinReading(readings, angles, (float) (Math.PI/2), (float) (Math.PI/12));
		float front = SonarUtils.getReading(0f, readings, angles);
		float left = SonarUtils.getReading((float) (Math.PI/2), readings, angles);
		float leftFront = SonarUtils.getReading((float) (Math.PI/4), readings, angles);
		if (minFront < OBSTACLE_FOUND_THRS){
			angular = -WF_ROT_VEL_OBS_FRONT;
			linear = 0;
		} else {
			// Get the current relation and the target relation (wall parallel
			// to robot)
			float quot = left / leftFront;
			float targetquot = (float) Math.cos(Math.PI / 8);

			float close_prop = Math.min(MAX_ERR, left - WL_RIGHT_TARGET);
			
			angular = PROP_ANG_PARALLEL * (targetquot - quot) + PROP_ANG_WALL_CLOSE * close_prop;

			float front_err = Math.min(front - WL_FW_TARGET, MAX_ERR);
			linear = Math.max(PROP_LINEAR_WF * (front_err), WF_MIN_FW_VEL);
		}
		
		return new Velocities(linear, angular);
	}
	
	public static Velocities wallFollowLeft(Float1dPort readings, Float1dPort angles) {
		float linear, angular; 
		
		float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI/3));
		float minRight = SonarUtils.getMinReading(readings, angles, (float) (-Math.PI/2), (float) (Math.PI/6));
		float front = SonarUtils.getReading(0f, readings, angles);
		float right = SonarUtils.getReading((float) (-Math.PI/2), readings, angles);
		float rightFront = SonarUtils.getReading((float) (-Math.PI/4), readings, angles);
		if (minFront < OBSTACLE_FOUND_THRS || minRight >= 1.5*OBSTACLE_FOUND_THRS){
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
		
		return new Velocities(linear, angular);
	}
}
