package edu.usf.ratsim.nsl.modules.actionselection;

import java.util.List;

import edu.usf.experiment.robot.FeederRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.EatAffordance;
import edu.usf.experiment.robot.affordance.ForwardAffordance;
import edu.usf.experiment.robot.affordance.LocalActionAffordanceRobot;
import edu.usf.experiment.robot.affordance.TurnAffordance;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;

public class FeederTraveler extends Module {

	private FeederRobot fr;
	private LocalActionAffordanceRobot ar;
	private List<Integer> feedersToVisit;

	public FeederTraveler(String name, List<Integer> feederOrder, Robot robot) {
		super(name);

		this.fr = (FeederRobot) robot;
		this.ar = (LocalActionAffordanceRobot) robot;
		
		feedersToVisit = feederOrder;
	}

	@Override
	public void run() {
		List<Feeder> allFeeders = fr.getVisibleFeeders();
		Feeder next = null;
		if (feedersToVisit.size() == 0)
			return;
		for (Feeder f : allFeeders){
			if (f.getId() == feedersToVisit.get(0))
				next = f;
		}

		if (next == null){
			System.out.println("Next feeder not found: " + feedersToVisit.get(0));
			String listOfFeeders = "List of feeders: ";
			for(Feeder f : allFeeders){
				listOfFeeders += f.getId() + ", ";
				if (f.getId() == feedersToVisit.get(0))
					next = f;
			}
			System.out.println(listOfFeeders);
			return;
		}
		
		if (GeomUtils.distanceToPoint(next.getPosition()) < ar.getStepLength()){
			ar.executeAffordance(new EatAffordance());
			feedersToVisit.remove(0);
		} else {
			float angleToPoint = GeomUtils.rotToAngle(GeomUtils.angleToPoint(next
					.getPosition()));
			if (Math.abs(angleToPoint) > ar.getMinAngle()) {
				ar.executeAffordance(new TurnAffordance(ar.getMinAngle()
						* Math.signum(angleToPoint), 0));
			} else {
				ar.executeAffordance(
						new ForwardAffordance(ar.getStepLength()));
			}
		}
		
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
