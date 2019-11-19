package com.github.biorobaw.scs.tasks.cycle.condition;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.experiment.ExperimentController;
import com.github.biorobaw.scs.tasks.cycle.CycleTask;
import com.github.biorobaw.scs.utils.files.XML;

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
	
	abstract protected boolean condition();

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
