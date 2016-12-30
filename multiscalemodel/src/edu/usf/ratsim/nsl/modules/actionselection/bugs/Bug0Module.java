
package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;

public class Bug0Module extends Module {

	private static final float MIN_ANGLE_DIFF_THRS = (float) (Math.PI / 16);
	private static final float FREE_PASSAGE_THRS = 1.5f * BugUtilities.OBSTACLE_FOUND_THRS;

	private VirtualRobot r;

	private enum State {
		GOAL_SEEKING, WALL_FOLLOWING
	};

	private State state;

	public Bug0Module(String name, Subject sub) {
		super(name);

		this.r = (VirtualRobot) sub.getRobot();

		
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
			if (readings.get(2) < BugUtilities.OBSTACLE_FOUND_THRS) {
				state = State.WALL_FOLLOWING;
			}
			break;
		case WALL_FOLLOWING:
			// Get the relative angle to the goal
			// TODO: angleToPointWithOrientation seems to be returning negated
			// angles (to the left angles should be positive)
			float angleToGoal = -GeomUtils.angleToPointWithOrientation(GeomUtils.angleToRot(rOrient.get()), rPos.get(),
					platPos.get());
			// Get the closest sonar
			float minAngleDiff = Float.MAX_VALUE;
			int minSonar = 0;
			for (int i = 0; i < angles.getSize(); i++) {
				float angleDiff = Math.abs(GeomUtils.angleDiff(angles.get(i), angleToGoal));
				if (angleDiff < minAngleDiff) {
					minAngleDiff = angleDiff;
					minSonar = i;
				}
			}
			// If a sonar is close enough and the reading shows free passage,
			// switch
			if (minAngleDiff < MIN_ANGLE_DIFF_THRS && readings.get(minSonar) > FREE_PASSAGE_THRS)
				state = State.GOAL_SEEKING;

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
		case WALL_FOLLOWING:
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

	@Override
	public void newEpisode() {
		super.newEpisode();
		
		state = State.GOAL_SEEKING;
	}

	
}
