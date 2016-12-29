package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class Bug1Module extends Module {

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
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		// State switching criteria
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			if (readings.get(2) < BugUtilities.OBSTACLE_FOUND_THRS * 1.1f) {
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
			if (distToHP > BugUtilities.CLOSE_THRS) {
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
			if (distToHP < BugUtilities.CLOSE_THRS) {
				state = State.WF_GO_TO_CP;
			}
			break;
		case WF_GO_TO_CP:
			float distToCP = rPos.get().distance(minDistPlace);
			if (distToCP < BugUtilities.CLOSE_THRS) {
				state = State.GOAL_SEEKING;
			}
			break;
		}

		// Common variabls
		float left = readings.get(0);
		float leftFront = readings.get(1);
		float front = readings.get(2);
		Velocities v = new Velocities();
		// Cmd depending on state
		switch (state) {
		case GOAL_SEEKING:
			v = BugUtilities.goalSeek(rPos.get(), rOrient.get(), platPos.get());
			break;
		case WF_AWAY_FROM_HP:
		case WF_RETURN_TO_HP:
		case WF_GO_TO_CP:
			v = BugUtilities.wallFollow(left, leftFront, front);
			break;
		}

		// Enforce maximum velocities
		v.trim();

		// Execute commands
		if (v.angular != 0)
			r.rotate(v.angular);
		if (v.linear != 0)
			r.forward(v.linear);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
	
	public void newEpisode(){
		state = State.GOAL_SEEKING;
		hitPoint = null;
		minDistPlace = null;
	}

}
