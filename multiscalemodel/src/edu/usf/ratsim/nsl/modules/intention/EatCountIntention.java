package edu.usf.ratsim.nsl.modules.intention;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.utils.Debug;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Implements intention cells. It provides an output port with an array of
 * cells. Only one cell is at most active at any given time. 
 * 
 * This intention module changes intentions each time the subject eats.
 * 
 * @author Martin Llofriu
 *
 */
public class EatCountIntention extends Module implements Intention {

	public float[] intention;
	private Subject sub;
	private int ate;

	public EatCountIntention(String name, int numIntentions, Subject sub) {
		super(name);
		intention = new float[numIntentions];
		addOutPort("intention", new Float1dPortArray(this, intention));
		
		this.sub = sub;
		this.ate = 0;
	}

	public void run() {
		if (sub.hasEaten())
			ate++;

		run(ate);
	}

	public void run(int intentionNumber) {
		for (int i = 0; i < intention.length; i++)
			intention[i] = 0;

		// System.out.println(goalFeeder.get());
		if (intentionNumber != -1 && intentionNumber < intention.length)
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

	public void reset() {
		ate = 0;
	}

}
