package edu.usf.experiment.robot.componentInterfaces;

import java.util.List;

import edu.usf.experiment.universe.Feeder;

public interface FeederVisibilityInterface {
	
	public List<Feeder> getVisibleFeeders(int[] except);
	public Feeder getClosestFeeder();

}
