package edu.usf.ratsim.nsl.modules.intention;

import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Implements intention cells. It provides an output port with an array of
 * cells. Only one cell is at most active at any given time. The module receives
 * the last visited (eat) feeder as input.
 * 
 * @author Martin Llofriu
 *
 */
public class LastAteIntention extends Module implements Intention {

	public float[] intention;

	public LastAteIntention(String name, int numIntentions) {
		super(name);
		intention = new float[numIntentions];
		addOutPort("intention", new Float1dPortArray(this, intention));
	}

	public void run() {
		Int0dPort goalFeeder = (Int0dPort) getInPort("goalFeeder");

		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;

		run(goalFeeder.get());
	}

	public void run(int intentionNumber) {
		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;

		// System.out.println(goalFeeder.get());
		if (intentionNumber != -1)
			intention[intentionNumber] = 1;

		if (Debug.printIntention) {
			for (int i = 0; i < intention.length; i++)
				System.out.print(intention[i] + " ");
			System.out.println();
		}
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
