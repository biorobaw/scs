package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.RobotAction;
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePortMap;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Bool0dPort;
import edu.usf.ratsim.nsl.modules.actionselection.ActionFromProbabilities;
import edu.usf.ratsim.nsl.modules.port.ModelActionPort;

public class ReservoirActionSelectionModule extends Module 
{

	//FeederTaxicAction action = new FeederTaxicAction(-1);
	MoveToAction action = new MoveToAction(0.0f, 0.0f, 0.0f, 1.0f);
	//action = new DifferentialNavigationAction(leftSpeed,RightSpeed)
	ModelActionPort outport = new ModelActionPort(this, action);
	Reservoir reservoir;

	public ReservoirActionSelectionModule(String name, Reservoir reservoir) {
		super(name);
		this.reservoir = reservoir;
 		reservoir.finishInitialization(new PositionLoop(), new StimulusLoop());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run()
	{
		Bool0dPort finishedAction = (Bool0dPort)getInPort("finishedAction");
		
		if (finishedAction.get())
		{
			
			
			Point3f next_position = reservoir.get_next_position();
			if (next_position != null)
			{
				action.setX(next_position.x);
				action.setY(next_position.y);
				System.out.println("moving robot to position "  + next_position.x + ", " + next_position.y);
			}
			else
			{
				Point3f pos = ((Point3fPort)getInPort("position")).get();
				float activation_pattern[] = ((Float1dSparsePortMap)getInPort("placeCells")).getData();
		
				reservoir.set_current_position(pos, activation_pattern);
			}
			
		}
	
		
	}
	
	
	

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void newEpisode(){

	}
	
	
	class PositionLoop extends TRN4JAVA.Custom.Simulation.Loop
	{
		@Override
		public void callback(final long id, final long trial, final long evaluation, final float prediction[], final long rows, final long cols)
		{
			assert(rows == 1);
			assert(cols == 2);
			
			reservoir.append_next_position(id, trial, evaluation, new Point3f(prediction[0], prediction[1], 0.0f));
		}	
	}
	class StimulusLoop extends TRN4JAVA.Custom.Simulation.Loop
	{
		@Override
		public void callback(final long id, final long trial, final long evaluation, final float prediction[], final long rows, final long cols)
		{
			System.out.println("RESERVOIR PREDICTED STIMULUS");
		}
	}
}
