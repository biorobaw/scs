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
public class DeallocReservoirTask extends Task {

	int simulation_number;
	short condition_number;
	private Integer runLevel;
	
	public DeallocReservoirTask(ElementWrapper params) {
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
		
		if(runLevel==0) return;
		/*TRN4JAVA.Basic.Simulation.Identifier identifier = new TRN4JAVA.Basic.Simulation.Identifier((short)1, (short)condition_number, simulation_number);

		long simulation_id = TRN4JAVA.Basic.Simulation.encode(identifier);*/
		//TRN4JAVA.Extended.Simulation.deallocate(simulation_id);
		
		TRN4JAVA.Basic.Engine.uninitialize();
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
