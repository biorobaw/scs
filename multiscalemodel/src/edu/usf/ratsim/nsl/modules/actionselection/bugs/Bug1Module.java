package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.HolonomicRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.support.SonarUtils;

public class Bug1Module extends Module {

	private HolonomicRobot r;

	private enum State {
		GOAL_SEEKING, WF_AWAY_FROM_HP, WF_RETURN_TO_HP, WF_GO_TO_CP
	};

	private State state;
	private float minDistToGoal;
	private Coordinate minDistPlace;
	private Coordinate hitPoint;
	private float obstFoundThrs;

	public Bug1Module(String name, Robot robot, float obstFoundThrs) {
		super(name);

		// TODO: set to differential robot?
		this.r = (HolonomicRobot) robot;
		this.obstFoundThrs = obstFoundThrs;
		
		state = State.GOAL_SEEKING;
	}
	
	public Bug1Module(String name, Robot robot) {
		this(name, robot, BugUtilities.DEFAULT_OBSTACLE_FOUND_THRS);
	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		PointPort rPos = (PointPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		PointPort platPos = (PointPort) getInPort("platformPosition");

		// State switching criteria
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			float minFront = SonarUtils.getMinReading(readings, angles, 0f, (float) (Math.PI/6));
			if (minFront < obstFoundThrs) {
				state = State.WF_AWAY_FROM_HP;
				minDistToGoal = (float) rPos.get().distance(platPos.get());
				minDistPlace = rPos.get();
				hitPoint = rPos.get();
			}
			break;
		// Wall following getting away from the hitpoint at first
		case WF_AWAY_FROM_HP:
			// Record min dist
			float distToGoal = (float) rPos.get().distance(platPos.get());
			if (distToGoal < minDistToGoal) {
				minDistToGoal = distToGoal;
				minDistPlace = rPos.get();
			}

			float distToHP = (float) rPos.get().distance(hitPoint);
			if (distToHP > BugUtilities.CLOSE_THRS) {
				state = State.WF_RETURN_TO_HP;
			}
			break;
		// Wall following until the hitpoint is found again
		case WF_RETURN_TO_HP:
			// Record min dist
			distToGoal = (float) rPos.get().distance(platPos.get());
			if (distToGoal < minDistToGoal) {
				minDistToGoal = distToGoal;
				minDistPlace = rPos.get();
			}

			distToHP = (float) rPos.get().distance(hitPoint);
			if (distToHP < BugUtilities.CLOSE_THRS) {
				state = State.WF_GO_TO_CP;
			}
			break;
		// Wall following until the close point is found again
		case WF_GO_TO_CP:
			float distToCP = (float) rPos.get().distance(minDistPlace);
			if (distToCP < BugUtilities.CLOSE_THRS) {
				state = State.GOAL_SEEKING;
			}
			break;
		}
//		System.out.println(state);
		
		// Cmd depending on state
		Velocities v = new Velocities();
		switch (state) {
		case GOAL_SEEKING:
			v = BugUtilities.goalSeek(rPos.get(), rOrient.get(), platPos.get());
			break;
		case WF_AWAY_FROM_HP:
		case WF_RETURN_TO_HP:
		case WF_GO_TO_CP:
			v = BugUtilities.wallFollowRight(readings, angles, r.getRadius(), obstFoundThrs);
			break;
		}

		
		// Enforce maximum velocities
		v.trim();

		// Execute commands
		r.setVels(v.x, v.y, v.theta);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	public void newEpisode() {
		state = State.GOAL_SEEKING;
		hitPoint = null;
		minDistPlace = null;
	}

}
