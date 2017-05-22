package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**

 * 
 * @author biorob
 * 
 */
public class ObstacleEndTaxic extends Module {

	private float[] votes;
	private Subject sub;
	private float taxicVal;
	private AffordanceRobot ar;
	private WallRobot wr;
	private float negReward;
	private float tooCloseDist;

	public ObstacleEndTaxic(String name, Robot robot,
			float taxicVal, float negReward, float tooCloseDist) {
		super(name);
		
		this.ar = (AffordanceRobot) robot;
		this.wr = (WallRobot) robot;
		
		votes = new float[ar.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.taxicVal = taxicVal;
		this.negReward = negReward;
		this.tooCloseDist = tooCloseDist;
	}

	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		List<Point3f> interestingPoints = wr.getVisibleWallEnds();

		for (Point3f p : interestingPoints) {
			if (p.distance(new Point3f()) > tooCloseDist) {
				// For each affordance set a value based on current interest
				// point
				List<Affordance> affs = ar.checkAffordances(ar
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
							if (angleDiff < wr.getHalfFieldView())
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
	}

	private float getPointValue(Point3f feederPos, float reward) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return (float) Math.max(0, (reward + negReward * steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
