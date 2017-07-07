package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.StepRobot;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUtils;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

public class GoToFeeder extends Module {

	private FeederRobot fr;
	private AffordanceRobot ar;
	private StepRobot sr;
	private Random random;
	private Int0dPort takenActionPort;
	private Subject sub;

	public GoToFeeder(String name, Robot robot, Random random) {
		super(name);

		this.fr = (FeederRobot) robot;
		this.ar = (AffordanceRobot) robot;
		this.sr = (StepRobot) robot;
		this.random = random;
		
		takenActionPort = new Int0dPort(this);
		addOutPort("takenAction", takenActionPort);
	}

	@Override
	public void run() {
		Float1dPort votes = (Float1dPort) getInPort("votes");

		List<Feeder> visibleFeeders = fr.getVisibleFeeders();

		// Get action value for each visible feeder
		List<ActionValue> avList = new LinkedList<ActionValue>();
		for (Feeder f : visibleFeeders){
			// Votes at 0 are for feeder 1
			avList.add(new ActionValue(f.getId(), votes.get(f.getId())));
			System.out.print(f.getId() + " " + votes.get(f.getId()));
		}
		System.out.println();
		
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
				sr.rotate(120);
			else
				sr.rotate(-120);
			takenActionPort.set(-1);
		} else {
			int desiredFeederId = avList.get(0).getAction();
			if (fr.isFeederClose()
					&& FeederUtils.getClosestFeeder(fr.getVisibleFeeders()).getId() == desiredFeederId){
				ar.executeAffordance(new EatAffordance());
			} else {
				Feeder desiredFeeder = null;
				for (Feeder f : visibleFeeders)
					if (f.getId() == desiredFeederId)
						desiredFeeder = f;
				// Todo: make it a step
				sr.rotate(GeomUtils
						.angleToPoint(desiredFeeder.getPosition()));
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
