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
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class GoToFeeder extends Module {

	private LocalizableRobot lRobot;
	private Random random;
	private Int0dPort takenActionPort;
	private Subject sub;

	public GoToFeeder(String name, Subject sub, Random random) {
		super(name);

		this.lRobot = (LocalizableRobot) sub.getRobot();
		this.sub = sub;
		this.random = random;
		
		takenActionPort = new Int0dPort(this);
		addOutPort("takenAction", takenActionPort);
	}

	@Override
	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");

		List<Feeder> visibleFeeders = lRobot.getVisibleFeeders(null);

		// Get action value for each visible feeder
		List<ActionValue> avList = new LinkedList<ActionValue>();
		for (Feeder f : visibleFeeders)
			// Votes at 0 are for feeder 1
			avList.add(new ActionValue(f.getId(), votes.get(f.getId() - 1)));
		// Sort avList by value
		Collections.sort(avList);
		
		// Check whether the robot is seeing the currently seek feeder
		boolean seeToGoFeeder = false;
		if (!avList.isEmpty()){
			for (Feeder f : visibleFeeders){
				seeToGoFeeder |= f.getId() == avList.get(0).getAction();
			}
		}

		// If not seeing the seek feeder
		if (!seeToGoFeeder) {
			// Turn 120
			if (random.nextBoolean())
				lRobot.rotate(120);
			else
				lRobot.rotate(-120);
			takenActionPort.set(-1);
		} else {
			int desiredFeederId = avList.get(0).getAction();
			if (lRobot.isFeederClose()
					&& lRobot.getClosestFeeder().getId() == desiredFeederId){
				lRobot.executeAffordance(new EatAffordance(), sub);
			} else {
				Feeder desiredFeeder = null;
				for (Feeder f : visibleFeeders)
					if (f.getId() == desiredFeederId)
						desiredFeeder = f;
				// Todo: make it a step
				lRobot.rotate(GeomUtils.rotToAngle(GeomUtils
						.angleToPoint(desiredFeeder.getPosition())));
				// Parametrize
				lRobot.forward(.1f);
			}
			
			takenActionPort.set(desiredFeederId);

		}

	}

	@Override
	public boolean usesRandom() {
		return true;
	}

}
