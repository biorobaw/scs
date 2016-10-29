package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.Float2dPort;

public class GoToFeeder extends Module {

	private LocalizableRobot lRobot;
	private Random random;
	private Int0dPort takenActionPort;
	private Subject sub;
	private int desiredFeederId;
	private int numActions;

	public GoToFeeder(String name, Subject sub, Random random, int numActions) {
		super(name);

		this.lRobot = (LocalizableRobot) sub.getRobot();
		this.sub = sub;
		this.random = random;
		
		takenActionPort = new Int0dPort(this);
		addOutPort("takenAction", takenActionPort);
		
		desiredFeederId = -1;
		this.numActions = numActions;
	}

	@Override
	public void run() {
		Bool0dPort subTriedToEat = (Bool0dPort) getInPort("subTriedToEat");
		Int0dPort lastTriedToEatFeeder = (Int0dPort) getInPort("lastTriedToEatFeeder");
		Float2dPort value = (Float2dPort) getInPort("value");
		
		if (subTriedToEat.get() || desiredFeederId == -1){
			float maxVotes = Float.NEGATIVE_INFINITY;
//			System.out.print("State: " + lastTriedToEatFeeder.get() + " ");
			for (int a = 0; a < numActions; a++){
//				System.out.print(a + ": " + value.get(lastTriedToEatFeeder.get(), a) + " ");
				if (value.get(lastTriedToEatFeeder.get(), a) > maxVotes){
					maxVotes = value.get(lastTriedToEatFeeder.get(), a);
					desiredFeederId = a + 1;
				}
			}
//			System.out.println();	
		}
		
//		System.out.println("Desired feeder id " + desiredFeederId);
		
		if (lRobot.isFeederClose()
				&& lRobot.getClosestFeeder().getId() == desiredFeederId){
			lRobot.executeAffordance(new EatAffordance(), sub);
//			System.out.println("Trying to eat");
		} else {
			Feeder desiredFeeder = null;
			for (Feeder f : lRobot.getVisibleFeeders(null))
				if (f.getId() == desiredFeederId)
					desiredFeeder = f;
			// Todo: make it a step
//			System.out.println("Desired feeder " + desiredFeeder);
			lRobot.rotate(GeomUtils.rotToAngle(GeomUtils
					.angleToPoint(desiredFeeder.getPosition())));
			// Parametrize
			lRobot.forward(.1f);
			
			
		}
		
		takenActionPort.set(desiredFeederId-1);

		

	}

	@Override
	public boolean usesRandom() {
		return true;
	}

}
