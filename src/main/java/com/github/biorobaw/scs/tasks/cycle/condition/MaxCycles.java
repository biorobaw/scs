package com.github.biorobaw.scs.tasks.cycle.condition;

import com.github.biorobaw.scs.utils.files.XML;

/**
 * This conditions returns true whenever cycle count goes above a certain number
 * @author ludo, bucef
 *
 */
public class MaxCycles extends Condition {

	private long maxCycles;

	public MaxCycles(XML xml) {
		super(xml);
		maxCycles = xml.getLongAttribute("cycles");
	}

	
	
	@Override
	public boolean condition() {
//		System.out.println("Checking cycle: " + sim.get_simulation_cycle());
		return controller.getSimulationCycle()>=maxCycles;
	}
	


}
