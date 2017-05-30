package edu.usf.vlwsim.discrete;

import java.awt.geom.Point2D.Float;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;

public class DiscreteRobot implements LocalizableRobot, PlatformRobot, TeleportRobot {

	private DiscreteVirtualUniverse u;

	public DiscreteRobot(ElementWrapper params, Universe u) {
		this.u = (DiscreteVirtualUniverse) u;
	}
	
	@Override
	public void startRobot() {
	}

	@Override
	public void setPosition(Point3f pos) {
		u.setRobotPosition(new Float(pos.x, pos.y));
	}

	@Override
	public boolean hasFoundPlatform() {
		return PlatformUniverseUtilities.hasRobotFoundPlatformDiscrete(u.getPlatforms(), u.getRobotPosition());
	}

	@Override
	public Point3f getPosition() {
		return u.getRobotPosition();
	}

	@Override
	public float getOrientationAngle() {
		return u.getRobotOrientationAngle();
	}

}
