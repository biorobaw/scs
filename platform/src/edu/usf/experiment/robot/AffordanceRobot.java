package edu.usf.experiment.robot;

import java.util.List;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;

public interface AffordanceRobot extends Robot {

	/**
	 * Checks each passed affordance to decide if it is realizable or not
	 * @param possibleAffordances
	 * @return
	 */
	public abstract List<Affordance> checkAffordances(List<Affordance> possibleAffordances);
	
	public abstract boolean checkAffordance(Affordance af);

	public abstract void executeAffordance(Affordance selectedAction, Subject sub);
}
