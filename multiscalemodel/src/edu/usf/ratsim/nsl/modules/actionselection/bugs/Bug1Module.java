package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class Bug1Module extends Module {

	private static final float OBSTACLE_FOUND_THRS = .3f;
	private static final float PROP_ANG_PARALLEL = .2f;
	private static final float PROP_ANG_WALL_CLOSE = .1f;
	private static final float PROP_LINEAR_WF = 0.05f;
	private static final float PROP_LINEAR_GS = 0.01f;
	private static final float PROP_ANGULAR_GS = 0.1f;

	// TODO: this should be enforced by the robot
	private static final float MAX_ANGULAR = .5f;
	private static final float MAX_LINEAR = 0.1f;
	private static final float WL_FW_TARGET = OBSTACLE_FOUND_THRS - .1f;
	private static final float WF_MIN_FW_VEL = .01f;
	private static final float CLOSE_THRS = .2f;

	private VirtualRobot r;

	private enum State {
		GOAL_SEEKING, WF_AWAY_FROM_HP, WF_RETURN_TO_HP, WF_GO_TO_CP
	};

	private State state;
	private float minDistToGoal;
	private Point3f minDistPlace;
	private Point3f hitPoint;

	public Bug1Module(String name, Subject sub) {
		super(name);

		this.r = (VirtualRobot) sub.getRobot();

		state = State.GOAL_SEEKING;
	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		// State switching criteria
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			if (readings.get(2) < OBSTACLE_FOUND_THRS * 1.1f) {
				state = State.WF_AWAY_FROM_HP;
				minDistToGoal = rPos.get().distance(platPos.get());
				minDistPlace = rPos.get();
				hitPoint = rPos.get();
			}
			break;
		case WF_AWAY_FROM_HP:
			// Record min dist
			float distToGoal = rPos.get().distance(platPos.get());
			if (distToGoal < minDistToGoal) {
				minDistToGoal = distToGoal;
				minDistPlace = rPos.get();
			}

			float distToHP = rPos.get().distance(hitPoint);
			if (distToHP > CLOSE_THRS) {
				state = State.WF_RETURN_TO_HP;
			}
			break;
		case WF_RETURN_TO_HP:
			// Record min dist
			distToGoal = rPos.get().distance(platPos.get());
			if (distToGoal < minDistToGoal) {
				minDistToGoal = distToGoal;
				minDistPlace = rPos.get();
			}

			distToHP = rPos.get().distance(hitPoint);
			if (distToHP < CLOSE_THRS) {
				state = State.WF_GO_TO_CP;
			}
			break;
		case WF_GO_TO_CP:
			float distToCP = rPos.get().distance(minDistPlace);
			if (distToCP < CLOSE_THRS) {
				state = State.GOAL_SEEKING;
			}
			break;
		}

		// Common variabls
		float front = readings.get(2);
		float linear = 0;
		float angular = 0;
		// Cmd depending on state
		switch (state) {
		case GOAL_SEEKING:
			linear = PROP_LINEAR_GS * platPos.get().distance(rPos.get());
			angular = -PROP_ANGULAR_GS * GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(rOrient.get()),
					rPos.get(), platPos.get());
			break;
		case WF_AWAY_FROM_HP:
		case WF_RETURN_TO_HP:
		case WF_GO_TO_CP:
			float left = readings.get(0);
			float leftfw = readings.get(1);

			// Get the current relation and the target relation (wall parallel
			// to robot)
			float quot = left / leftfw;
			float targetquot = (float) Math.cos(Math.PI / 8);

			float close_prop = left - WL_FW_TARGET;

			angular = PROP_ANG_PARALLEL * (targetquot - quot) + PROP_ANG_WALL_CLOSE * close_prop;

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
