package edu.usf.ratsim.nsl.modules.taxic;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.ratsim.micronsl.Float1dPortArray;
import edu.usf.ratsim.micronsl.Module;
import edu.usf.ratsim.support.GeomUtils;

public class TaxicWallOpeningsSchema extends Module {

	private static final float OPENING_MAX_LENGHT = 0.2f;
	private static final float OPENING_MIN_LENGHT = 0.02f;
	public float[] votes;
	private float reward;

	private Subject subject;
	private LocalizableRobot robot;

	public TaxicWallOpeningsSchema(String name, Subject subject,
			LocalizableRobot robot, float reward) {
		super(name);

		this.reward = reward;

		votes = new float[subject.getPossibleAffordances().size() + 1];
		addOutPort("votes", new Float1dPortArray(this, votes));

		this.subject = subject;
		this.robot = robot;
	}

	/**
	 * Assigns a fixed value to go to a wall end. Rationale: go to where walls
	 * end.
	 */
	public void run() {
		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		// Get the votes for each affordable action
		List<Affordance> affs = robot.checkAffordances(subject
				.getPossibleAffordances());
		int voteIndex = 0;

		List<Point3f> openings = getOpenings(robot.getVisibleWallEnds());

		for (Affordance af : affs) {
			float value = 0;
			if (af.isRealizable()) {
				if (af instanceof TurnAffordance) {
					for (Point3f p : openings) {
						value += getFeederValue(GeomUtils.simulate(p, af));
					}
				} else if (af instanceof ForwardAffordance) {
					for (Point3f p : openings) {
						value += getFeederValue(GeomUtils.simulate(p, af));
					}
				} else if (af instanceof EatAffordance) {
				} else
					throw new RuntimeException("Affordance "
							+ af.getClass().getName()
							+ " not supported by robot");
			}

			votes[voteIndex] = value;
			voteIndex++;
		}

		// This module does not intervene in value estimation
		votes[subject.getPossibleAffordances().size()] = 0;
	}

	private List<Point3f> getOpenings(List<Point3f> wEnds) {
		List<Point3f> openings = new LinkedList<Point3f>();
		for (int i = 0; i < wEnds.size(); i++)
			// Start one off to avoid comparing agains same wall
			for (int j = i + 2; j < wEnds.size(); j++) {
				if (wEnds.get(i).distance(wEnds.get(j)) < OPENING_MAX_LENGHT
						&& wEnds.get(i).distance(wEnds.get(j)) > OPENING_MIN_LENGHT) {
					Point3f midPoint = new Point3f(wEnds.get(i));
					midPoint.interpolate(wEnds.get(j), .5f);
					openings.add(midPoint);
				}
			}
		return openings;
	}

	private float getFeederValue(Point3f feederPos) {
		float steps = GeomUtils.getStepsToFeeder(feederPos, subject);
		return (float) (reward * Math.pow(.9f, steps));
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
