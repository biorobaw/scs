package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.List.Int1dPortList;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class NonVisitedFeederSetModule extends Module {

	FeederTaxicAction action = new FeederTaxicAction(-1);
	
	LinkedHashSet<Integer> visited = new LinkedHashSet<Integer>();
	
	LinkedList<Integer> subSelection = new LinkedList<Integer>();
	Int1dPortList outport = new Int1dPortList(this, subSelection);
	
	float filterProbability = 0;
	
	public NonVisitedFeederSetModule(String name,float _filterProbability) {
		super(name);
		
		
		addOutPort("feederSubSet", outport);

		filterProbability =  _filterProbability;
		
	}

	@Override
	public void run() {
		
		//grabs a set of feeders and filters out all already visited feeders with probability P
		//note: the current feeder is always filtered out
		
		int currentFeeder = ((Int0dPort)getInPort("currentFeeder")).get();
		if(currentFeeder!=-1) visited.add(currentFeeder);
		
		
		Int1dPortList feederSet = (Int1dPortList)getInPort("feederSet");
		subSelection.clear();
		
		
		if(RandomSingleton.getInstance().nextFloat() < filterProbability){
			
//			System.out.println("FILTERING");
			
			for(int i = 0;i<feederSet.getSize();i++) 
				if(!visited.contains(feederSet.get(i))) subSelection.add(feederSet.get(i));
		} else {
			
//			System.out.println("SKIPPING FILTERING");
			for(int i = 0;i<feederSet.getSize();i++) if(feederSet.get(i)!=currentFeeder ) subSelection.add(feederSet.get(i));
		}
		

		
	}
	

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		visited.clear();
		super.newEpisode();
	}
	


	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}

}
