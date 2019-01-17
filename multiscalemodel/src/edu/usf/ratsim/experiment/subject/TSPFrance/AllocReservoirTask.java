package edu.usf.ratsim.experiment.subject.TSPFrance;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Globals;
import edu.usf.experiment.Trial;
import edu.usf.experiment.task.Task;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Task to deactivate all enabled feeders
 * @author ludo
 *
 */
public class AllocReservoirTask extends Task {


	int simulation_number;
	short condition_number;
	
	Integer runLevel;
	
	public AllocReservoirTask(ElementWrapper params) 
{
		super(params);
		
		condition_number = (short)params.getChildInt("condition_number");
		simulation_number =  params.getChildInt("simulation_number");
		
		String component = (String)Globals.getInstance().get("component");
		if(component==null ) runLevel = 2; // full model
		else if (component.equals("scs")) runLevel = 0; // only scs
		else if (component.equals("reservoir")) runLevel = 1; //only reservoir

		

	}

	@Override
	public void perform(Experiment experiment) {

	}

	@Override
	public void perform(Trial trial) {
		//perform();
	}

	@Override
	public void perform(Episode episode) {
		//perform(episode.getUniverse());
	}
	
	private void perform(TSPSubjectFrance sub){
		//release reservoir 
		
		
	}

}
