package edu.usf.experiment.robot.componentInterfaces;

import java.util.AbstractCollection;
import java.util.List;

import edu.usf.experiment.universe.Feeder;

public interface FeederVisibilityInterface {
	
	public List<Feeder> getVisibleFeeders(int[] except);
	public Feeder getClosestFeeder();
	
	public int getClosestFeeder(AbstractCollection<Integer> feederSet);

}
