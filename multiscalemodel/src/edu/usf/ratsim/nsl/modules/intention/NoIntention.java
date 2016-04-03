package edu.usf.ratsim.nsl.modules.intention;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**
 * Dummy intention module that always sets the same intention.
 * 
 * @author biorob
 *
 */
public class NoIntention extends Module implements Intention {

	public float[] intention;

	public NoIntention(String name, int numIntentions) {
		super(name);
		intention = new float[numIntentions];
		addOutPort("intention", new Float1dPortArray(this, intention));
	}

	public void run() {
		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;

		intention[0] = 1;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

	@Override
	public void run(int inte) {
		run();
	}
}
