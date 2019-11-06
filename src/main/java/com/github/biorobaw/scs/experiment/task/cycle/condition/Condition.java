package com.github.biorobaw.scs.experiment.task.cycle.condition;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.experiment.ExperimentController;
import com.github.biorobaw.scs.experiment.task.cycle.CycleTask;
import com.github.biorobaw.scs.utils.XML;

/**
 * Conditions signal whether a certain condition holds or not. They usually
 * query information about the subject and the universe.
 * 
 * @author ludo
 *
 */
public abstract class Condition extends CycleTask {

	protected ExperimentController controller;
	
	public Condition(XML xml) {
		super(xml);
		controller = Experiment.get().controller;
	}
	
	abstract boolean condition();

	@Override
	public long perform() {
		if(condition()) controller.endEpisode();
		return 0;
	}
	
	@Override
	public int getPriority() {
		return 1000;
	}

}
