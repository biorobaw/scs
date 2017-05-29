package edu.usf.experiment.robot.affordance;

public class AbsoluteAngleDiscreteAffordance extends Affordance {
	
	public int dx;
	public int dy;
	
	public AbsoluteAngleDiscreteAffordance(int dx, int dy){
		this.dx = dx;
		this.dy = dy;
	}

	@Override
	public String toString() {
		return "AbsoluteAngleDiscreteAffordance " + dx + " " + dy;
	}

}
