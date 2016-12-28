package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class Bug1Module extends Module {

	private static final float OBSTACLE_FOUND_THRS = .3f;
	private static final float PROP_ANG_PARALLEL = .2f;
	private static final float PROP_ANG_WALL_CLOSE = .1f;
	private static final float PROP_LINEAR_WF = 0.05f;
	
	// TODO: this should be enforced by the robot
	private static final float MAX_ANGULAR = .5f;
	private static final float MAX_LINEAR = 0.1f;
	private static final float WL_FW_TARGET = OBSTACLE_FOUND_THRS - .1f;
	private static final float WF_MIN_FW_VEL = .01f;
	
	private VirtualRobot r;
	
	private enum State { GOAL_SEEKING, WALL_FOLLOWING };
	private State state;

	public Bug1Module(String name, Subject sub) {
		super(name);

		this.r = (VirtualRobot) sub.getRobot();
		
		state = State.GOAL_SEEKING;
	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		
		// State switching criteria
		switch (state){
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			if (readings.get(2) < OBSTACLE_FOUND_THRS * 1.1f)
				state = State.WALL_FOLLOWING;
			break;
		case WALL_FOLLOWING:
			break;
		}
		
		// Common variabls
		float front = readings.get(2);
		float linear = 0; 
		float angular = 0;
		// Cmd depending on state
		switch (state){
		case GOAL_SEEKING:
			linear = PROP_LINEAR_WF * (front - OBSTACLE_FOUND_THRS);
			break;
		case WALL_FOLLOWING:
			float left = readings.get(0);
			float leftfw = readings.get(1);
			
			// Get the current relation and the target relation (wall parallel to robot)
			float quot = left / leftfw;
			float targetquot = (float) Math.cos(Math.PI/8);
			
			float close_prop = left - WL_FW_TARGET;
			
			angular = PROP_ANG_PARALLEL * (targetquot - quot)
					+ PROP_ANG_WALL_CLOSE * close_prop;
			
			linear = Math.min(PROP_LINEAR_WF * (front - WL_FW_TARGET), WF_MIN_FW_VEL);
			break;
		}
		
		angular = Math.min(MAX_ANGULAR, Math.max(angular, -MAX_ANGULAR));
		linear = Math.min(MAX_LINEAR, Math.max(linear, -MAX_LINEAR));
		
		if (angular != 0)
			r.rotate(angular);
		if (linear != 0)
			r.forward(linear);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
