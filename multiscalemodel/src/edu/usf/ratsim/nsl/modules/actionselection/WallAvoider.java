package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**
 * Sets the dopaminergic votes for both a flashing feeder and a non flashing
 * one.
 * 
 * @author ludo
 * 
 */
public class WallAvoider extends Module {

	private static final float EPS_VALUE = 1f;
	private static final float DIMINISH_FACTOR = .8f;
	private static final int WALL_LOOKAHEAD = 10;
	public float[] votes;
	private float wallFollowingValue;
	private Robot robot;
	private float currentValue;
	private boolean active;
	private int direction;
	// private int number;
	private Subject subject;

	public WallAvoider(String name, Subject subject, float wallFollowingValue,
			int numActions) {
		super(name);
		this.subject = subject;
		this.wallFollowingValue = wallFollowingValue;
		this.currentValue = 0f;
		active = false;

		// number = (new Random()).nextInt();

		votes = new float[numActions + 1];
		addOutPort("votes", new Float1dPortArray(this, votes));

		robot = subject.getRobot();
		this.subject = subject;
	}

	public void run() {
		// System.out.println("Performing wall avoider " + number);

		for (int i = 0; i < votes.length; i++)
			votes[i] = 0;

		List<Affordance> affs = robot.checkAffordances(subject
				.getPossibleAffordances());
		int index = 0;
		for (Affordance af : affs) {
			if (!af.isRealizable())
				votes[index] = wallFollowingValue;
			index++;
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}
}
