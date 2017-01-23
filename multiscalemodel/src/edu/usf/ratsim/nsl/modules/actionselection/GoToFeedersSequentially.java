package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class GoToFeedersSequentially extends Module {

	private LocalizableRobot lRobot;
	private Random random;
	private Int0dPort takenActionPort;
	private SubjectOld sub;
	private int togo;

	public GoToFeedersSequentially(String name, SubjectOld sub, Random random) {
		super(name);

		this.lRobot = (LocalizableRobot) sub.getRobot();
		this.sub = sub;
		this.random = random;
		
		takenActionPort = new Int0dPort(this);
		addOutPort("takenAction", takenActionPort);
		
		togo = 1;
	}

	@Override
	public void run() {
		List<Feeder> visibleFeeders = lRobot.getVisibleFeeders(null);
		
		boolean seeToGoFeeder = false;
		for (Feeder f : visibleFeeders){
			seeToGoFeeder |= f.getId() == togo;
		}

		if (!seeToGoFeeder) {
			// Turn 120
			if (random.nextBoolean())
				lRobot.rotate(120);
			else
				lRobot.rotate(-120);
			takenActionPort.set(-1);
		} else {
			int desiredFeederId = togo;
			if (lRobot.isFeederClose()
					&& lRobot.getClosestFeeder().getId() == desiredFeederId){
				lRobot.executeAffordance(new EatAffordance(), sub);
				togo++;
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
