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

	public abstract void executeAffordance(Affordance selectedAction);

	public abstract List<Affordance> getPossibleAffordances();

	public abstract float getMinAngle();

	public abstract float getStepLength();

	public abstract Affordance getForwardAffordance();

	public abstract Affordance getLeftAffordance();

	public abstract Affordance getRightAffordance();
}
