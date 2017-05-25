package edu.usf.experiment.task.feeder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.universe.feeder.Feeder;
import edu.usf.experiment.universe.feeder.FeederUniverse;
import edu.usf.experiment.utils.CSVReader;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;
import edu.usf.experiment.utils.RandomSingleton;

public class AddNFeedersFromFileTask extends Task{
	public Globals global = Globals.getInstance();
	public String feedersFile;
	private int numFeeders;

	public AddNFeedersFromFileTask(ElementWrapper params) {
		super(params);
		// TODO Auto-generated constructor stub
		
		feedersFile = params.getChildText("feederFile");			
		numFeeders = params.getChildInt("numFeeders");
			
		//if not in globals
		if(feedersFile==null){
			System.err.println("WARNING: no feeder file");
		}
		
	}
	
	public void perform(Universe u, Subject s){
		if (!(u instanceof FeederUniverse))
			throw new IllegalArgumentException("");
		
		FeederUniverse fu = (FeederUniverse) u;
		
		
		//parse feederfile and create feeders
		if (feedersFile==null) return;
		
		// All feeders from file
		List<Feeder> allFeeders = new LinkedList<Feeder>();
		
		String[][] feederData = CSVReader.loadCSV(feedersFile, ",","feedersFile");
		if (feederData!=null){
			IOUtils.copyFile(feedersFile, global.get("logPath") + "feeders.txt");
			
			Integer id = 0;
			for (String[] line : feederData){
				if(line.length == 2){
					id++;
					Float x = Float.parseFloat(line[0]);
					Float y = Float.parseFloat(line[1]);
					allFeeders.add(new Feeder(id, new Point3f(x, y, 0)));
				}
			}
		}

		// Pick 5 random feeders
		Random r = RandomSingleton.getInstance();
		// Feeder numbers start from 1 - 0 is no feeeder
		for (int i = 1; i <= numFeeders; i++){
			int item = r.nextInt(allFeeders.size());
			Feeder f = allFeeders.remove(item);
			// Set the id incremental to avoid gaps
			f.setId(i);
			fu.addFeeder(f.getId(), f.getPosition().x, f.getPosition().y);
		}
			
		
	}
	

}
