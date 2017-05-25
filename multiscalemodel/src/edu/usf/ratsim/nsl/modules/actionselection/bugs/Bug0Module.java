
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
		GOAL_SEEKING, WALL_FOLLOWING
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

		float front = SonarUtils.getReading(0f, readings, angles);
		float left = SonarUtils.getReading((float) (Math.PI / 2), readings, angles);
		float leftFront = SonarUtils.getReading((float) (Math.PI / 4), readings, angles);

		// State switching criteria

		// Get the relative angle to the goal
		// TODO: angleToPointWithOrientation seems to be returning negated
		// angles (to the left angles should be positive)
		float angleToGoal = -GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(rOrient.get()), rPos.get(),
				platPos.get());
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			if (SonarUtils.validSonar(angleToGoal, readings, angles)
					&& SonarUtils.getReading(angleToGoal, readings, angles) < FREE_PASSAGE_THRS) {
				state = State.WALL_FOLLOWING;
			}
			break;
		case WALL_FOLLOWING:
			// If a sonar is close enough and the reading shows free passage,
			// switch
			if (SonarUtils.validSonar(angleToGoal, readings, angles)
					&& SonarUtils.getReading(angleToGoal, readings, angles) > FREE_PASSAGE_THRS)
				state = State.GOAL_SEEKING;

			break;
		}

		// Cmd depending on state
		Velocities v = new Velocities();
		switch (state) {
		case GOAL_SEEKING:
			v = BugUtilities.goalSeek(rPos.get(), rOrient.get(), platPos.get());
			break;
		case WALL_FOLLOWING:
			v = BugUtilities.wallFollow(left, leftFront, front);
			break;
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
