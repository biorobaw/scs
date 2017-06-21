package edu.usf.vlwsim.discrete;

import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.robot.affordance.AbsoluteAngleDiscreteAffordance;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class FourNAbsDirDiscreteRobot extends DiscreteRobot implements AffordanceRobot {

	private DiscreteVirtualUniverse u;
	
	public FourNAbsDirDiscreteRobot(Universe u) {
		super(u);
		
		this.u = (DiscreteVirtualUniverse) u;
	}

	public FourNAbsDirDiscreteRobot(ElementWrapper params, Universe u) {
		super(params, u);
		
		this.u = (DiscreteVirtualUniverse) u;
	}

	@Override
	public List<Affordance> checkAffordances(List<Affordance> affordances) {
		for (Affordance a : affordances){
			checkAffordance(a);
		}
			
		return affordances;
	}

	@Override
	public float checkAffordance(Affordance af) {
		boolean realizable = false;
		
		if (af instanceof AbsoluteAngleDiscreteAffordance){
			AbsoluteAngleDiscreteAffordance aada = (AbsoluteAngleDiscreteAffordance) af;
			realizable = u.canRobotMove(aada.dx, aada.dy);
			af.setRealizable(realizable);
		}
		
		return realizable ? 1 : 0;
	}

	@Override
	public void executeAffordance(Affordance af) {
		if (af instanceof AbsoluteAngleDiscreteAffordance){
			AbsoluteAngleDiscreteAffordance aada = (AbsoluteAngleDiscreteAffordance) af;
			u.setMotion(aada.dx, aada.dy);
		}
	}

	@Override
	public List<Affordance> getPossibleAffordances() {
		LinkedList<Affordance> affs = new LinkedList<Affordance>();
		affs.add(new AbsoluteAngleDiscreteAffordance(1, 0));
		affs.add(new AbsoluteAngleDiscreteAffordance(0, 1));
		affs.add(new AbsoluteAngleDiscreteAffordance(-1, 0));
		affs.add(new AbsoluteAngleDiscreteAffordance(0, -1));
		return affs;
	}

}
