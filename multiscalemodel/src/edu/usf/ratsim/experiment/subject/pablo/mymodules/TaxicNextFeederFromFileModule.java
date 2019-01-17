package edu.usf.ratsim.experiment.subject.pablo.mymodules;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.List.Int1dPortList;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class TaxicNextFeederFromFileModule extends Module {

	public FeederTaxicAction action = new FeederTaxicAction(-1);
	ModelActionPort outport = new ModelActionPort(this, action);
	
	ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
	int nextPath = 0;
	int nextFeeder = -1;
	
	public boolean completedPath() {
		return nextFeeder == paths.get(nextPath).size();
	}
	
		
	public TaxicNextFeederFromFileModule(String name,String fileName) {
		super(name);
		
		String[][] data = CSVReader.loadCSV(fileName, ",");
		
		for(String[] path : data) {
			ArrayList<Integer> iPath = new ArrayList<>();
			for(String feeder : path) {
				//System.out.println("feeder: " + feeder);
				iPath.add(Integer.parseInt(feeder.trim()));
			}
			paths.add(iPath);
		}
		
		addOutPort("action", outport);

		
		/*for(ArrayList<Integer> path : paths) {
			System.out.println("path: "+path);
		}*/

		
	}

	@Override
	public void run() {
		
		if(((Bool0dPort)getInPort("newSelection")).get()) {
			//System.out.println("SETTING ID: " + paths.get(nextPath).get(nextFeeder) + "  " + nextPath + " " + nextFeeder );
			action.setId(paths.get(nextPath).get( ++nextFeeder % paths.get(nextPath).size()));
			
		}
		

		
	}
	

	@Override
	public void newEpisode() {
		// TODO Auto-generated method stub
		super.newEpisode();
		nextFeeder = -1;
		
	}
	
	public void nextPathInList() {
		nextPath = (nextPath+1)%paths.size();
	}
	
	


	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}

}
