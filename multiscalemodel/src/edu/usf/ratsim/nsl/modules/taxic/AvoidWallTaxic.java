package edu.usf.ratsim.nsl.modules.taxic;

import java.util.List;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Module;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class AvoidWallTaxic extends Module {

	private float[] votes;
	private Subject sub;
	private float taxicVal;
	private LocalizableRobot robot;
	private float distToConsider;

	public AvoidWallTaxic(String name, Subject sub, LocalizableRobot robot,
			float taxicVal, float distToConsider) {
		super(name);
		votes = new float[sub.getPossibleAffordances().size()];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.sub = sub;
		this.taxicVal = taxicVal;
		this.distToConsider = distToConsider;
		this.robot = robot;

	}

	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		List<Affordance> affs = robot.checkAffordances(sub
				.getPossibleAffordances());
		int closeToNoseWalls = robot.closeToNoseWalls(distToConsider);
		int voteIndex = 0;
		for (Affordance af : affs) {
			float value = 0;
			if (af.isRealizable()) {
				if (af instanceof TurnAffordance) {
					// If leftAffordance and there is wall on the right (2 or 3)
					if ((closeToNoseWalls == 2 || closeToNoseWalls == 3)
							&& ((TurnAffordance) af).getAngle() > 0)
						value += taxicVal;
					// If rightAffordance and there is wall on the left (1 or 3)
					if ((closeToNoseWalls == 1 || closeToNoseWalls == 3)
							&& ((TurnAffordance) af).getAngle() < 0)
						value += taxicVal;
				} else if (af instanceof EatAffordance
						|| af instanceof ForwardAffordance) {
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

		if (Debug.printAvoidWallValues) {
			for (int i = 0; i < votes.length; i++)
				System.out.print(votes[i] + " ");
			System.out.println();
		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
