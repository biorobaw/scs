package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.AffordanceRobot;
import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.StepRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.FeederUtils;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class GoToFeedersSequentially extends Module {

	private FeederRobot fr;
	private AffordanceRobot ar;
	private StepRobot sr;
	private Random random;
	private Int0dPort takenActionPort;
	private Subject sub;
	private int togo;

	public GoToFeedersSequentially(String name, Subject sub, Random random) {
		super(name);

		this.fr = (FeederRobot) sub.getRobot();
		this.ar = (AffordanceRobot) sub.getRobot();
		this.sr = (StepRobot) sub.getRobot();
		this.sub = sub;
		this.random = random;
		
		takenActionPort = new Int0dPort(this);
		addOutPort("takenAction", takenActionPort);
		
		togo = 1;
	}

	@Override
	public void run() {
		List<Feeder> visibleFeeders = fr.getVisibleFeeders();
		
		boolean seeToGoFeeder = false;
		for (Feeder f : visibleFeeders){
			seeToGoFeeder |= f.getId() == togo;
		}

		if (!seeToGoFeeder) {
			// Turn 120
			if (random.nextBoolean())
				sr.rotate(120);
			else
				sr.rotate(-120);
			takenActionPort.set(-1);
		} else {
			int desiredFeederId = togo;
			if (fr.isFeederClose()
					&& FeederUtils.getClosestFeeder(fr.getVisibleFeeders()).getId() == desiredFeederId){
				ar.executeAffordance(new EatAffordance(), sub);
				togo++;
			} else {
				Feeder desiredFeeder = null;
				for (Feeder f : visibleFeeders)
					if (f.getId() == desiredFeederId)
						desiredFeeder = f;
				// Todo: make it a step
				sr.rotate(GeomUtils.rotToAngle(GeomUtils
						.angleToPoint(desiredFeeder.getPosition())));
				// Parametrize
				sr.forward(.1f);
			}
			
			takenActionPort.set(desiredFeederId);

		}

	}

	@Override
	public boolean usesRandom() {
		return true;
	}

}
