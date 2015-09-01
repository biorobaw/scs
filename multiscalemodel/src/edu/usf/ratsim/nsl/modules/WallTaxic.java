package edu.usf.ratsim.nsl.modules;

import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.support.GeomUtils;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class WallTaxic extends Module {

	private float[] votes;
	private Subject sub;
	private float taxicVal;
	private LocalizableRobot robot;

	public WallTaxic(String name, Subject sub, LocalizableRobot robot,
			float taxicVal) {
		super(name);
		votes = new float[sub.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.sub = sub;
		this.taxicVal = taxicVal;
		this.robot = robot;
	}

	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		List<Point3f> interestingPoints = robot.getVisibleWallEnds();

		for (Point3f p : interestingPoints) {
			// For each affordance set a value based on current interest point
			List<Affordance> affs = robot.checkAffordances(sub
					.getPossibleAffordances());
			int voteIndex = 0;
			for (Affordance af : affs) {
				float value = 0;
				if (af.isRealizable()) {
					if (af instanceof TurnAffordance
							|| af instanceof ForwardAffordance) {
						Point3f newPos = GeomUtils.simulate(p, af);
						Quat4f rotToNewPos = GeomUtils.angleToPoint(newPos);

						float angleDiff = Math.abs(GeomUtils
								.rotToAngle(rotToNewPos));
						float feederVal;
						if (angleDiff < robot.getHalfFieldView())
							feederVal = getPointValue(newPos, taxicVal);
						else
							feederVal = -getPointValue(newPos, taxicVal);
						if (feederVal > value)
							value = feederVal;

					} else if (af instanceof EatAffordance) {
					} else
						throw new RuntimeException("Affordance "
								+ af.getClass().getName()
								+ " not supported by robot");
				}

				if (value != Float.NEGATIVE_INFINITY)
					votes[voteIndex] = value;
				else
					votes[voteIndex] = 0;
				voteIndex++;
			}
		}
	}

	private float getPointValue(Point3f feederPos, float reward) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, sub);
		if (steps < 10)
			return 0;
		else 
			return (float) (reward * Math.pow(.9, steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
