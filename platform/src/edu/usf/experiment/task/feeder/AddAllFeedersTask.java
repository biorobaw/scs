package edu.usf.experiment.task.feeder;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class AddAllFeedersTask extends Task{
	public Globals global = Globals.getInstance();
	public String feedersFile;

	public AddAllFeedersTask(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		feedersFile = params.getChildText("feederFile");			
			
		//if not in globals
		if(feedersFile==null){
			System.err.println("WARNING: no feeder file");
		}
		
		
		
	}

	public void perform(Universe u, Subject s) {
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		
		//parse feederfile and create feeders
		if (feedersFile==null) return;
		
		String[][] feederData = CSVReader.loadCSV(feedersFile, ",","feedersFile");
		if (feederData!=null){
			IOUtils.copyFile(feedersFile, global.get("logPath") + "feeders.txt");
			
			Integer id = 0;
			for (String[] line : feederData){
				if(line.length == 2){
					id++;
					Float x = Float.parseFloat(line[0]);
					Float y = Float.parseFloat(line[1]);
					fu.addFeeder(id, x, y);
					
				}
			}
		}

		
	}
	

}
