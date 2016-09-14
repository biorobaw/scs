package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;

public class GoToFeeder extends Module {

	private LocalizableRobot lRobot;
	private Random random;

	public GoToFeeder(String name, List<Integer> feederOrder,
			LocalizableRobot lRobot, Random random) {
		super(name);

		this.lRobot = lRobot;
		this.random = random;
	}

	@Override
	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");

		List<Feeder> allFeeders = lRobot.getVisibleFeeders(null);

		// Get action value for each visible feeder
		List<ActionValue> avList = new LinkedList<ActionValue>();
		for (Feeder f : allFeeders)
			avList.add(new ActionValue(f.getId(), votes.get(f.getId())));
		// Sort avList by value
		Collections.sort(avList);

		if (avList.isEmpty()) {
			// Turn 120
			if (random.nextBoolean())
				lRobot.rotate(120);
			else
				lRobot.rotate(-120);
		} else {
			// If desired feeder close
			// Eat
			// Else
			// Go to most voted feeder
			int desiredFeederId = avList.get(0).getAction();
			if (lRobot.isFeederClose()
					&& lRobot.getClosestFeeder().getId() == desiredFeederId)
				lRobot.eat();
			else {
				Feeder desiredFeeder = null;
				for (Feeder f : allFeeders)
					if (f.getId() == desiredFeederId)
						desiredFeeder = f;
				// Todo: make it a step
				lRobot.rotate(GeomUtils.rotToAngle(GeomUtils
						.angleToPoint(desiredFeeder.getPosition())));
				// Parametrize
				lRobot.forward(.1f);
			}

		}

	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
