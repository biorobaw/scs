package edu.usf.experiment.condition;

import edu.usf.experiment.utils.ElementWrapper;

/**
 * This conditions returns true whenever cycle count goes above a certain number
 * @author ludo
 *
 */
public class MaxCycles extends Condition {

	private int maxCycles;
	private int cycles;

	public MaxCycles(ElementWrapper params) {
		maxCycles = params.getChildInt("max");
		cycles = 0;
	}

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		cycles = 0;
	}
	
	@Override
	public boolean holds() {
		cycles++;

		return cycles >= maxCycles;
	}
	


}
