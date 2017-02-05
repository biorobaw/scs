package edu.usf.ratsim.experiment.subject.TSPFrance;

import edu.usf.micronsl.module.Module;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class SchemeSelector extends Module {
	

	public SchemeSelector(String name) {
		super(name);

		//addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		//throw new IllegalArgumentException("Argument 'divisor' is Infinity");
		//Float.POSITIVE_INFINITY

	}

	public void run() {
		//Float1dPortArray input = (Float1dPortArray) getInPort("input");

		//System.out.println("I'n scheme selector");


	}


	@Override
	public boolean usesRandom() {
		return true;
	}
}
