package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * This module receives the action votes as an input port and performs the
 * action that is realizable and has the most votes.
 * 
 * @author Martin Llofriu
 * 
 */
public class NoExploration extends Module {

	/**
	 * The robot interface used to perform actions
	 */
	private Robot robot;
	/**
	 * The subject, used to get all possible affordances
	 */
	private Subject sub;
	/**
	 * The output port describing the action taken by the module
	 */
	private Int0dPort takenAction;

	/**
	 * Create the module
	 * 
	 * @param name
	 *            The module's name
	 * @param sub
	 *            The subject to use
	 */
	public NoExploration(String name, Subject sub) {
		super(name);

		takenAction = new Int0dPort(this);
		addOutPort("takenAction", takenAction);

		robot = sub.getRobot();

		this.sub = sub;
	}

	/**
	 * Gets the votes input port, assigns the value to each affordance
	 */
	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");

		Affordance selectedAction;
		List<Affordance> aff = robot.checkAffordances(sub.getPossibleAffordances());

		for (int action = 0; action < aff.size(); action++) {
			aff.get(action).setValue(votes.get(action));
			if (Debug.printSelectedValues)
				System.out.println("votes for aff " + action + ": " + votes.get(action));
		}

		// Select best action
		List<Affordance> sortedAff = new LinkedList<Affordance>(aff);
		Collections.sort(sortedAff);
		selectedAction = sortedAff.get(aff.size() - 1);

		// Only execute realizable actions unless rotation (rotations are always
		// truly realizable and still explorer might vote for "non-realizable"
		// rotations
		if (selectedAction instanceof TurnAffordance || selectedAction.isRealizable()) {
			takenAction.set(aff.indexOf(selectedAction));
			if (Debug.printSelectedValues)
				System.out.println(selectedAction.toString());

			robot.executeAffordance(selectedAction, sub);
		} else {
			System.err.println("No realizable affordances, skiping execution");
		}
		// } else {
		// takenAction.set(-1);
		// }

		// TODO: get the rotation -> forward back
		// // System.out.println(takenAction.get());
		// lastRot = selectedAction == sub.getActionLeft()
		// || selectedAction == sub.getActionRight();
	}

	@Override
	public boolean usesRandom() {
		// Executes robot movements, which uses random
		return true;
	}
}
