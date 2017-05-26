package edu.usf.vlwsim.robot;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.AbsoluteDirectionRobot;
import edu.usf.experiment.robot.affordance.AbsoluteAngleAffordance;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.universe.wall.WallUniverseUtilities;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.vlwsim.universe.AbsoluteDirectionRobotVirtualUniverse;

public class AbsoluteDirectionVirtualRobot extends VirtualRobot implements AbsoluteDirectionRobot, AffordanceRobot {

	private AbsoluteDirectionRobotVirtualUniverse universe;
	/**
	 * Possible angles to move towards in an absolute frame of reference
	 */
	private List<Float> motionAngles;
	/**
	 * The move step
	 */
	private float step;
	/**
	 * Maximum sensing distance for affordance checking options
	 */
	private float maxSenseDistance;

	public AbsoluteDirectionVirtualRobot(ElementWrapper params, Universe u) {
		super(params, u);
		
		universe = (AbsoluteDirectionRobotVirtualUniverse) u;
		
		motionAngles = params.getChildFloatList("motionAngles");
		step = params.getChildFloat("step");
		maxSenseDistance = params.getChildFloat("maxSenseDistance");
	}
	
	@Override
	public void setDirection(float absoluteAngle) {
		universe.setRobotNavDirection(absoluteAngle);
	}

	@Override
	public void setADStep(float step) {
		universe.setRobotADStep(step);
	}

	@Override
	public void executeAffordance(Affordance af) {
		if (af instanceof AbsoluteAngleAffordance) {
			AbsoluteAngleAffordance aaa = (AbsoluteAngleAffordance) af;
			setDirection(aaa.getAngle());
			setADStep(aaa.getDistance());
		} else if (af instanceof EatAffordance) {
			if (isFeederClose()) {
				eat(); // TODO: should the robot check for feeder close or just
						// execute action
			}

		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> affs) {
		for (Affordance af : affs) {
			checkAffordance(af);
		}

		return affs;
	}

	@Override
	public float checkAffordance(Affordance af) {
		float realizable;
		if (af instanceof AbsoluteAngleAffordance) {
			AbsoluteAngleAffordance aaa = (AbsoluteAngleAffordance) af;
			// Get the distance to the wall in that angle
			float dist = WallUniverseUtilities.distanceToNearestWall(universe.getWalls(), universe.getRobotPosition(), aaa.getAngle(), maxSenseDistance);
			// If we cannot even do the step, the affordance should not be realizable
			float distAfterStep = Math.max(0, dist - aaa.getDistance());
			// The afforance is fully realizable if there is nothing on the sensor reach
			realizable = distAfterStep / maxSenseDistance;
		} else if (af instanceof EatAffordance) {
			// realizable = hasRobotFoundFood();
			if (FeederUtils.getClosestFeeder(getVisibleFeeders()) != null)
				realizable = FeederUtils.getClosestFeeder(getVisibleFeeders()).getPosition()
						.distance(new Point3f()) < getCloseThrs() ? 1 : 0;
			else
				realizable = 0;
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");

		af.setRealizable(realizable);
		return realizable;
	}

	@Override
	public List<Affordance> getPossibleAffordances() {
		List<Affordance> res = new LinkedList<Affordance>();

		for (Float angle : motionAngles)
			res.add(new AbsoluteAngleAffordance(angle, step));
		res.add(new EatAffordance());

		return res;
	}


}
