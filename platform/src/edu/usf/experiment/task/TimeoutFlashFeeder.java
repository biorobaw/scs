package edu.usf.experiment.task;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.RandomSingleton;

/**
 * Task to deactivate a random feeder from the set of enabled ones
 * 
 * @author ludo
 * 
 */
public class TimeoutFlashFeeder extends Task {

	private Random random;
	private int timeout;
	private int timeSinceAte;

	public TimeoutFlashFeeder(ElementWrapper params) {
		super(params);
		random = RandomSingleton.getInstance();
		timeout = params.getChildInt("timeout");
		timeSinceAte = 0;
	}

	public void perform(Universe u, Subject s){
		if (s.hasEaten())
			timeSinceAte = 0;
		else
			timeSinceAte++;
 
//		System.out.println("Time since ate " + timeSinceAte + " timeout " + timeout);
		if (timeSinceAte == timeout) {
			List<Integer> active = u.getActiveFeeders();

			// Pick an active one and flash
			int i = random.nextInt(active.size());
			System.out.println("Flashing feeder " + active.get(i));
			int toFlash = active.get(i);
			u.setFlashingFeeder(toFlash, true);
		}
	}

}
