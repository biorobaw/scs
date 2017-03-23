package edu.usf.ratsim.nsl.modules.actionselection.bugs;

import javax.vecmath.Point3f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import edu.usf.experiment.subject.Subject;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.robot.virtual.VirtualRobot;
import edu.usf.ratsim.support.SonarUtils;

public class Bug2Module extends Module {

	private VirtualRobot r;

	private enum State {
		GOAL_SEEKING, WF_AWAY_FROM_ML, WF_RETURN_TO_ML
	};

	private State state;
	private LineSegment mLine;
	private Point3f hitPoint;

	public Bug2Module(String name, Subject sub) {
		super(name);

		this.r = (VirtualRobot) sub.getRobot();

		state = State.GOAL_SEEKING;

		mLine = null;
	}

	@Override
	public void run() {
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");
		Point3fPort rPos = (Point3fPort) getInPort("position");
		Float0dPort rOrient = (Float0dPort) getInPort("orientation");
		Point3fPort platPos = (Point3fPort) getInPort("platformPosition");

		if (mLine == null) {
			mLine = new LineSegment(new Coordinate(rPos.get().x, rPos.get().y),
					new Coordinate(platPos.get().x, platPos.get().y));
		}

		float front = SonarUtils.getReading(0f, readings, angles);
		float left = SonarUtils.getReading((float) (Math.PI / 2), readings, angles);
		float leftFront = SonarUtils.getReading((float) (Math.PI / 4), readings, angles);

		// State switching criteria
		switch (state) {
		case GOAL_SEEKING:
			// Check the middle sensor for obstacles
			if (front < BugUtilities.OBSTACLE_FOUND_THRS) {
				state = State.WF_AWAY_FROM_ML;
				hitPoint = rPos.get();
			}
			break;
		case WF_AWAY_FROM_ML:
			// Record min dist
			float distToMLine = (float) mLine.distancePerpendicular(new Coordinate(rPos.get().x, rPos.get().y));
			if (distToMLine > BugUtilities.CLOSE_THRS) {
				state = State.WF_RETURN_TO_ML;
			}
			break;
		case WF_RETURN_TO_ML:
			// Record min dist
			distToMLine = (float) mLine.distancePerpendicular(new Coordinate(rPos.get().x, rPos.get().y));
			// If it reaches the m-line and is closer than the first time
			if (distToMLine < BugUtilities.CLOSE_THRS && rPos.get()
					.distance(platPos.get()) < (hitPoint.distance(platPos.get()) - BugUtilities.CLOSE_THRS)) {
				state = State.GOAL_SEEKING;
			}
			break;
		}

		// Cmd depending on state
		Velocities v = new Velocities();
		switch (state) {
		case GOAL_SEEKING:
			v = BugUtilities.goalSeek(rPos.get(), rOrient.get(), platPos.get());
			break;
		case WF_AWAY_FROM_ML:
		case WF_RETURN_TO_ML:
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

		mLine = null;
		hitPoint = null;
		state = State.GOAL_SEEKING;
	}

}
