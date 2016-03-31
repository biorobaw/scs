package edu.usf.ratsim.nsl.modules;

import java.util.List;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.Module;

public class FeederTraveler extends Module {

	private LocalizableRobot lRobot;
	private Subject sub;
	private List<Integer> feedersToVisit;

	public FeederTraveler(String name, List<Integer> feederOrder, Subject sub, LocalizableRobot lRobot) {
		super(name);

		this.lRobot = lRobot;
		this.sub = sub;
		
		feedersToVisit = feederOrder;
	}

	@Override
	public void run() {
		List<Feeder> allFeeders = lRobot.getVisibleFeeders(null);
		Feeder next = null;
		for (Feeder f : allFeeders){
			if (f.getId() == feedersToVisit.get(0))
				next = f;
		}

		if (next == null){
			System.out.println("Next feeder not found");
			return;
		}
		
		if (GeomUtils.distanceToPoint(next.getPosition()) < sub.getStepLenght()){
			lRobot.executeAffordance(new EatAffordance(), sub);
			feedersToVisit.remove(0);
		} else {
			float angleToPoint = GeomUtils.rotToAngle(GeomUtils.angleToPoint(next
					.getPosition()));
			if (Math.abs(angleToPoint) > sub.getMinAngle()) {
				lRobot.executeAffordance(new TurnAffordance(sub.getMinAngle()
						* Math.signum(angleToPoint), 0), sub);
			} else {
				lRobot.executeAffordance(
						new ForwardAffordance(sub.getStepLenght()), sub);
			}
		}
		
	}

	@Override
	public boolean usesRandom() {
		return false;
	}

}
