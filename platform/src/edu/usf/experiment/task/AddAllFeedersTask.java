package edu.usf.experiment.task;


import java.util.HashSet;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class AddAllFeedersTask extends Task{
	public Globals global = Globals.getInstance();
	public String feedersFile;
	HashSet<Integer> selected;
	
	

	public AddAllFeedersTask(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		feedersFile = params.getChildText("feederFile");	
		
		if(params.getChildText("selected")==null) {
			selected = null;
		}
		else {
			selected = new HashSet<>();
			selected.addAll( params.getChildIntList("selected"));
		}
		
		//if not in globals
		if(feedersFile==null){
			System.err.println("WARNING: no feeder file");
		}
		
		
		
	}

	@Override
	public void perform(Experiment experiment) {
		// TODO Auto-generated method stub
		perform(experiment.getUniverse());
		
	}

	@Override
	public void perform(Trial trial) {
		// TODO Auto-generated method stub
		perform(trial.getUniverse());
	}

	@Override
	public void perform(Episode episode) {
		// TODO Auto-generated method stub
		perform(episode.getUniverse());
		
	}
	
	private void perform(Universe univ){
		//parse feederfile and create feeders
		if (feedersFile==null) return;
		
		String[][] feederData = CSVReader.loadCSV(feedersFile, ",","feedersFile");
		if (feederData!=null){
			IOUtils.copyFile(feedersFile, global.get("logPath") + "/feeders.txt");
			
			Integer id = 0;
			for (String[] line : feederData){
				if(line.length == 2){
					id++;
					Float x = Float.parseFloat(line[0]);
					Float y = Float.parseFloat(line[1]);
					if(selected==null || selected.contains(id))
						univ.addFeeder(id, x, y);
					
				}
			}
		}

		
	}
	

}
