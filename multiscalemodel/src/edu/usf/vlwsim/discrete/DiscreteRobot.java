package edu.usf.vlwsim.discrete;

import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.GlobalWallRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.PlatformRobot;
import edu.usf.experiment.robot.TeleportRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.platform.PlatformUniverseUtilities;
import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public class DiscreteRobot implements LocalizableRobot, PlatformRobot, TeleportRobot, GlobalWallRobot {

	private static final float ROBOT_RADIUS = 0.03f;
	
	private DiscreteVirtualUniverse u;
	
	public DiscreteRobot(Universe u) {
		this.u = (DiscreteVirtualUniverse) u;
	}

	public DiscreteRobot(ElementWrapper params, Universe u) {
		this.u = (DiscreteVirtualUniverse) u;
	}
	
	@Override
	public void startRobot() {
	}

	@Override
	public void setPosition(Coordinate pos) {
		u.setRobotPosition(pos);
	}

	@Override
	public boolean hasFoundPlatform() {
		return PlatformUniverseUtilities.hasRobotFoundPlatformDiscrete(u.getPlatforms(), u.getRobotPosition());
	}

	@Override
	public Coordinate getPosition() {
		return u.getRobotPosition();
	}

	@Override
	public float getOrientationAngle() {
		return u.getRobotOrientationAngle();
	}

	@Override
	public Set<Wall> getWalls() {
		return u.getWalls();
	}

	@Override
	public float getRadius() {
		return ROBOT_RADIUS;
	}

}
