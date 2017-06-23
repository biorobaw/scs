package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import javax.vecmath.Point3f;

import edu.usf.experiment.utils.GeomUtils;

public class BugUtilities {
	
	//TODO: change deltaT to be more realistic in seconds, and fix constants 
	public static final float OBSTACLE_FOUND_THRS = .2f;
	public static final float CLOSE_THRS = .2f;
	
	private static final float PROP_ANG_PARALLEL = .2f;
	private static final float PROP_ANG_WALL_CLOSE = .2f;
	private static final float PROP_LINEAR_WF = 0.1f;
	private static final float PROP_LINEAR_GS = 0.05f;
	private static final float PROP_ANGULAR_GS = 0.2f;

	private static final float WL_FW_TARGET = .15f;   	
	private static final float WF_MIN_FW_VEL = .01f;
	private static final float WF_ROT_VEL_OBS_FRONT = .2f;

	public static Velocities goalSeek(Point3f rPos, float rOrient, Point3f platPos) {
		float linear, angular; 
		linear = PROP_LINEAR_GS * platPos.distance(rPos);
		angular = -PROP_ANGULAR_GS * GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(rOrient),
				rPos, platPos);
		return new Velocities(linear, angular);
	}

	public static Velocities wallFollow(float left, float leftfw, float front) {
		float linear, angular; 
		
		if (front < OBSTACLE_FOUND_THRS){
			angular = -WF_ROT_VEL_OBS_FRONT;
			linear = 0;
		} else {
			// Get the current relation and the target relation (wall parallel
			// to robot)
			float quot = left / leftfw;
			float targetquot = (float) Math.cos(Math.PI / 8);

			float close_prop = left - WL_FW_TARGET;
			
			angular = PROP_ANG_PARALLEL * (targetquot - quot) + PROP_ANG_WALL_CLOSE * close_prop;

			linear = Math.min(PROP_LINEAR_WF * (front - WL_FW_TARGET), WF_MIN_FW_VEL);
		}
		
		return new Velocities(linear, angular);
	}
}
