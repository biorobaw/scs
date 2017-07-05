
package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import edu.usf.experiment.robot.DifferentialRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.support.SonarUtils;

public class Bug0Module extends Module {

	private static final float FREE_PASSAGE_THRS = 1.5f * BugUtilities.OBSTACLE_FOUND_THRS;

	private DifferentialRobot r;

	private enum State {
		GOAL_SEEKING, WALL_FOLLOWING_LEFT, WALL_FOLLOWING_RIGHT
	};

	private State state;

	public Bug0Module(String name, Robot robot) {
		super(name);

		// TODO: set to differential robot?
		this.r = (DifferentialRobot) robot;

	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		// State switching criteria

		// Get the relative angle to the goal
		// TODO: angleToPointWithOrientation seems to be returning negated
		// angles (to the left angles should be positive)
		float angleToGoal = -GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(rOrient.get()), rPos.get(),
				platPos.get());
		float minToGoal = SonarUtils.getMinReading(readings, angles, angleToGoal, (float) (Math.PI/6));
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			
			if (minToGoal < FREE_PASSAGE_THRS) {
				System.out.println(angleToGoal);
				if (angleToGoal > 0)
					state = State.WALL_FOLLOWING_RIGHT;
				else
					state = State.WALL_FOLLOWING_LEFT;
			}
			break;
		case WALL_FOLLOWING_LEFT:
		case WALL_FOLLOWING_RIGHT:
			// If a sonar is close enough and the reading shows free passage,
			// switch
			if (minToGoal >= FREE_PASSAGE_THRS)
				state = State.GOAL_SEEKING;

			break;
		}
		System.out.println(state);
		
		// Cmd depending on state
		Velocities v = new Velocities();
		switch (state) {
		case GOAL_SEEKING:
			v = BugUtilities.goalSeek(rPos.get(), rOrient.get(), platPos.get());
			break;
		case WALL_FOLLOWING_LEFT:
			v = BugUtilities.wallFollowLeft(readings,angles);
			break;
		case WALL_FOLLOWING_RIGHT:
			v = BugUtilities.wallFollowRight(readings, angles);
		}

		// Enforce maximum velocities
		v.trim();

		// Execute commands
		r.setAngularVel(v.angular);
		r.setLinearVel(v.linear);
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void newEpisode() {
		super.newEpisode();

		state = State.GOAL_SEEKING;
	}

}
