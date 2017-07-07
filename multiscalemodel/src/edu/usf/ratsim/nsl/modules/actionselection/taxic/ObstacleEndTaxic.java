package edu.usf.ratsim.nsl.modules.actionselection.taxic;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.WallRobot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.subject.Subject;
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
	private LocalActionAffordanceRobot ar;
	private WallRobot wr;
	private float negReward;
	private float tooCloseDist;

	public ObstacleEndTaxic(String name, Robot robot,
			float taxicVal, float negReward, float tooCloseDist) {
		super(name);
		
		this.ar = (LocalActionAffordanceRobot) robot;
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

		List<Coordinate> interestingPoints = wr.getVisibleWallEnds();

		for (Coordinate p : interestingPoints) {
			if (p.distance(new Coordinate()) > tooCloseDist) {
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
							Coordinate newPos = GeomUtils.simulate(p, af);
							float rotToNewPos = GeomUtils.angleToPoint(newPos);

							float angleDiff = Math.abs(rotToNewPos);
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

	private float getPointValue(Coordinate feederPos, float reward) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, ar.getMinAngle(), ar.getStepLength());
		return (float) Math.max(0, (reward + negReward * steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
