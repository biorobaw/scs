package edu.usf.experiment.condition;

/**
 * Conditions signal whether a certain condition holds or not. They usually
 * query information about the subject and the universe.
 * 
 * @author ludo
 *
 */
public interface Condition {

	public abstract boolean holds();
	

}
