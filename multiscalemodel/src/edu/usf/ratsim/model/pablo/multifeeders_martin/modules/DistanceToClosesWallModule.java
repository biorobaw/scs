package edu.usf.ratsim.model.pablo.multifeeders_martin.modules;

import edu.usf.experiment.robot.WallRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

public class DistanceToClosesWallModule  extends Module {

	private Float0dPort distance = new Float0dPort(this);
	private WallRobot robot;

	public DistanceToClosesWallModule(String name, WallRobot robot) {
		super(name);
		
		this.robot = (WallRobot)robot;
		addOutPort("distance", distance);
	}

	@Override
	public void run() {
		distance.set(robot.getDistanceToClosestWall());
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	
}