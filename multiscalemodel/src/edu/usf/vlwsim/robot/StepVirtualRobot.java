package edu.usf.vlwsim.robot;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.robot.StepRobot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.vlwsim.universe.StepRobotVirtualUniverse;

/**
 * This robot implements the step-wise motion for the VirtualRobot. It also 
 * @author martin
 *
 */
public class StepVirtualRobot extends VirtualRobot implements StepRobot, LocalActionAffordanceRobot {

	private float step;
	private float leftAngle;
	private float rightAngle;

	private float noise;
	private float translationRotationNoise;

	private float lookaheadSteps;

	private Random r;
	
	private StepRobotVirtualUniverse universe;

	public StepVirtualRobot(ElementWrapper params, Universe u) {
		super(params, u);

		/*
		 * Affordance parameters
		 */
		step = params.getChildFloat("step");
		leftAngle = params.getChildFloat("leftAngle");
		rightAngle = params.getChildFloat("rightAngle");

		noise = params.getChildFloat("noise");
		translationRotationNoise = params.getChildFloat("translationRotationNoise");
		lookaheadSteps = params.getChildFloat("lookaheadSteps");

		r = RandomSingleton.getInstance();
		
		universe = (StepRobotVirtualUniverse) u;
	}

	public void forward(float dist) {
		universe.setForwardDistance(dist + dist * r.nextFloat() * noise);
		universe.setTurnAngle((2 * r.nextFloat() - 1) * translationRotationNoise); // TODO: move noise to universe
		super.moved();
	}

	public void rotate(float grados) {
		universe.setTurnAngle(grados + noise * r.nextFloat() * grados);// TODO: move noise to universe
		super.moved();
	}

	@Override
	public void executeAffordance(Affordance af) {
		// WORKAROUND Dont execute the first cycle
		// TODO: fix this with better model-universe interaction
		if (PropertyHolder.getInstance().getProperty("cycle").equals("0"))
			return;

		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			List<Affordance> forward = new LinkedList<Affordance>();
			forward.add(new ForwardAffordance(ta.getDistance()));
			rotate(ta.getAngle());
		} else if (af instanceof ForwardAffordance) {
			forward(((ForwardAffordance) af).getDistance());
		} else if (af instanceof EatAffordance) {
			// Updates food in universe
			if (isFeederClose()) {
				eat(); // TODO: should the robot check for feeder close or just
						// execute action
			}

			// rotate((float) Math.PI);
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> affs) {
		for (Affordance af : affs) {
			boolean realizable;
			if (af instanceof TurnAffordance) {
				TurnAffordance ta = (TurnAffordance) af;
				// Either it can move there, or it cannot move forward and the
				// other angle is not an option
				realizable = !universe.canRobotMove(0, getRobotLength() * lookaheadSteps)
						// && !canRobotMove(-ta.getAngle(), ROBOT_LENGTH))
						|| universe.canRobotMove(ta.getAngle(), getRobotLength() * lookaheadSteps);
				// realizable = true;
			} else if (af instanceof ForwardAffordance)
				realizable = universe.canRobotMove(0, getRobotLength() * lookaheadSteps);
			else if (af instanceof EatAffordance) {
				// realizable = hasRobotFoundFood();
				if (FeederUtils.getClosestFeeder(getVisibleFeeders()) != null)
					realizable = FeederUtils.getClosestFeeder(getVisibleFeeders()).getPosition()
							.distance(new Point3f()) < getCloseThrs()
							&& FeederUtils.getClosestFeeder(getVisibleFeeders()).hasFood();
				// TODO: this is not good for MultiFeeders, where the robot
				// needs to eat on empty feeders to be disapointed- Fix
				else
					realizable = false;
			} else
				throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");

			af.setRealizable(realizable);
		}

		return affs;
	}

	@Override
	public float checkAffordance(Affordance af) {
		boolean realizable;
		if (af instanceof TurnAffordance) {
			TurnAffordance ta = (TurnAffordance) af;
			// Either it can move there, or it cannot move forward and the
			// other angle is not an option
			realizable = !universe.canRobotMove(0, getRobotLength() * lookaheadSteps)
					// && !canRobotMove(-ta.getAngle(), ROBOT_LENGTH))
					|| universe.canRobotMove(ta.getAngle(), getRobotLength() * lookaheadSteps);
			// realizable = true;
		} else if (af instanceof ForwardAffordance)
			realizable = universe.canRobotMove(0, getRobotLength() * lookaheadSteps);
		else if (af instanceof EatAffordance) {
			// realizable = hasRobotFoundFood();
			if (FeederUtils.getClosestFeeder(getVisibleFeeders()) != null)
				realizable = FeederUtils.getClosestFeeder(getVisibleFeeders()).getPosition()
						.distance(new Point3f()) < getCloseThrs();
			else
				realizable = false;
		} else
			throw new RuntimeException("Affordance " + af.getClass().getName() + " not supported by robot");

		af.setRealizable(realizable);
		return realizable ? 1 : 0;

	}

	@Override
	public List<Affordance> getPossibleAffordances() {
		List<Affordance> res = new LinkedList<Affordance>();

		res.add(getLeftAffordance());
		res.add(getForwardAffordance());
		res.add(getRightAffordance());
		res.add(new EatAffordance());

		return res;
	}

	@Override
	public float getMinAngle() {
		return Math.min(leftAngle, rightAngle);
	}

	@Override
	public float getStepLength() {
		return step;
	}

	@Override
	public Affordance getForwardAffordance() {
		return new ForwardAffordance(step);
	}

	@Override
	public Affordance getLeftAffordance() {
		return new TurnAffordance(leftAngle, step);
	}

	@Override
	public Affordance getRightAffordance() {
		return new TurnAffordance(rightAngle, step);
	}

}
