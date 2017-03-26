package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.BugUtilities;
import edu.usf.ratsim.nsl.modules.actionselection.bugs.Velocities;
import edu.usf.ratsim.support.SonarUtils;

/**
 * This module receives the action votes as an input port and performs the
 * action that is realizable and has the most votes.
 * 
 * @author Martin Llofriu
 * 
 */
public class NoExploration extends Module {

	private static final float STEP = 0.3f;
	private static final int CONTROL_ITERATIONS = 20;
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
	private Affordance lastAction;

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

		lastAction = null;

		this.sub = sub;
	}

	/**
	 * Gets the votes input port, assigns the value to each affordance
	 */
	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");
		Float1dPort readings = (Float1dPort) getInPort("sonarReadings");
		Float1dPort angles = (Float1dPort) getInPort("sonarAngles");

		Affordance selectedAction;
		List<Affordance> aff = sub.getPossibleAffordances();

		List<Affordance> possible = new LinkedList<Affordance>();
		float totalPossible = 0f;
		float minVal = 0;
		for (int action = 0; action < aff.size(); action++) {
			aff.get(action).setValue(votes.get(action));
			
//			System.out.print(votes.get(action) + " ");
		}
//		System.out.println();
		
		// Check only eating - needed to avoid executing the action in 0's case
		for (Affordance a : aff)
			if (a instanceof EatAffordance)
				a.setRealizable(robot.checkAffordance(a));
			else
				a.setRealizable(true);

		List<Affordance> sortedAff = new LinkedList<Affordance>(aff);
		Collections.sort(sortedAff);

		selectedAction = sortedAff.get(sortedAff.size()-1);
//		System.out.println(selectedAction);
		
		
		if (selectedAction instanceof EatAffordance){
			robot.executeAffordance(selectedAction, sub);
//			System.out.println("Eating");
		} else {
			float angle = 0;
			if (selectedAction instanceof ForwardAffordance)
				angle = 0;
			else if (selectedAction instanceof TurnAffordance)
				angle = ((TurnAffordance)selectedAction).getAngle();
			
			
			for (int i = 0; i < CONTROL_ITERATIONS; i++){
				float front = SonarUtils.getReading(0f, readings, angles);
				float left = SonarUtils.getReading((float) (Math.PI/2), readings, angles);
				float leftFront = SonarUtils.getReading((float) (Math.PI/4), readings, angles);
				Velocities v = new Velocities();	
				Point3f goal = new Point3f(STEP * (float) Math.cos(angle), STEP * (float)Math.sin(angle), 0);
				if (SonarUtils.getReading(angle, readings, angles) < BugUtilities.CLOSE_THRS/2)
					v = BugUtilities.wallFollow(left, leftFront, front, goal);
				else {
					
					v = BugUtilities.goalSeekRelative(goal);
				}
				if (v.angular != 0)
					robot.rotate(-v.angular);
				if (v.linear != 0)
					robot.forward(v.linear);
			}
		
				
		}
			

		lastAction = selectedAction;
		takenAction.set(aff.indexOf(selectedAction));

	}

	@Override
	public boolean usesRandom() {
		// Executes robot movements, which uses random
		return true;
	}
}
