package com.github.biorobaw.scs.tasks.cycle;

import com.github.biorobaw.scs.experiment.Experiment;
import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.files.XML;

public abstract class CycleTask implements Script {
	
	protected AbstractSimulator simulator = Experiment.get().simulator;
	
	public CycleTask(XML xml) {
	}
	
	@Override
	public int getPriority() {
		return 10;
	}
	
	/**
	 * Must return the amount of time in which to reschedule the task.
	 * Return -1 for not rescheduling,
	 * Return 0 for using the default simulation step
	 * Return a positive for any other amount of milliseconds
	 * @return
	 */
	abstract public long perform();
	
	@Override
	final public void run() {
		var delta_ms = perform();
		simulator.reschedule(delta_ms, this);
	}
}
