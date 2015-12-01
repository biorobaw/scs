package edu.usf.ratsim.nsl.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import sun.util.locale.StringTokenIterator;
import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.subject.affordance.TurnAffordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.ratsim.micronsl.Module;

public class FeederTraveler extends Module {

	private LocalizableRobot lRobot;
	private Subject sub;
	private List<Integer> feedersToVisit;

	public FeederTraveler(String name, Subject sub, LocalizableRobot lRobot) {
		super(name);

		this.lRobot = lRobot;
		this.sub = sub;
		
		PropertyHolder props = PropertyHolder.getInstance();
//		String feederListString = props.getProperty("feeder.order");
		StringTokenizer stringToken = new StringTokenizer("4,0,1,3,2", ",");
		feedersToVisit = new LinkedList<Integer>();
		while(stringToken.hasMoreElements())
			feedersToVisit.add(new Integer(stringToken.nextToken()));
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
