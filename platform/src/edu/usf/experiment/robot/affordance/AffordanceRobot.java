package edu.usf.experiment.robot.affordance;

import java.util.List;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;

public interface AffordanceRobot extends Robot {

	/**
	 * Checks each passed affordance to decide if it is realizable or not
	 * @param possibleAffordances
	 * @return
	 */
	public abstract List<Affordance> checkAffordances(List<Affordance> possibleAffordances);
	
	public abstract float checkAffordance(Affordance af);

	public abstract void executeAffordance(Affordance selectedAction);

	public abstract List<Affordance> getPossibleAffordances();

}
