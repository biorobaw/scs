
package edu.usf.ratsim.nsl.modules.actionselection.apf;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.BugUtilities;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Velocities;
import edu.usf.ratsim.robot.virtual.VirtualRobot;
import edu.usf.ratsim.support.SonarUtils;

public class APFModule extends Module {

	private static final float MIN_DIST_TO_OBS = 0.5f;

	private static final float REP_ANGULAR_P = 3f;
	
	private static final float ANGULAR_P = .5f;

	private static final float LINEAR_P = .5f;


	private static final float REP_LINEAR_P = 1f;

	private static final float CLOSE_THRS = .1f;

	private VirtualRobot r;

	private enum State {
		GOAL_SEEKING, WALL_FOLLOWING
	};

	private State state;

	public APFModule(String name, Subject sub) {
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

		float front = SonarUtils.getReading(0f, readings, angles);
		float leftFront = SonarUtils.getReading((float) (Math.PI/4), readings, angles);
		float rightFront = SonarUtils.getReading((float) (-Math.PI/4), readings, angles);

		float angleDiff = GeomUtils.angleToPointWithOrientation(rOrient.get(), rPos.get(), platPos.get());
		float minDist = Math.min(Math.min(front, leftFront), rightFront);
		
		// Cmd depending on state
		Velocities v = new Velocities();
		v.angular = -angleDiff * ANGULAR_P -Math.max(0, MIN_DIST_TO_OBS - leftFront) * REP_ANGULAR_P + Math.max(0, MIN_DIST_TO_OBS - rightFront) * REP_ANGULAR_P;
		if (minDist > CLOSE_THRS)
			v.linear = rPos.get().distance(platPos.get()) * LINEAR_P - Math.max(0, MIN_DIST_TO_OBS - minDist) * REP_LINEAR_P;
		else
			v.linear = 0;
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
