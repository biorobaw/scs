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

public class ReservoirActionSelectionModule extends Module {

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
		
			Point3f pos = ((Point3fPort)getInPort("position")).get();
			float activation_pattern[] = ((Float1dSparsePortMap)getInPort("placeCells")).getData();
			float estimated_position[] =  {pos.x, pos.y};
		
			reservoir.reinject(estimated_position, activation_pattern);
		}
		
		
	}
	
	
	

	@Override
	public boolean usesRandom() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void newEpisode(){

	}
	
	
	class PositionLoop extends TRN4JAVA.Api.Loop
	{
		@Override
		public void callback(final float[] predicted_position)
		{
			action.setX(predicted_position[0]);
			action.setY(predicted_position[1]);
			
			System.out.println("RESERVOIR PREDICTED COORDINATES (" + action.x() +", "+ action.y() +", " + action.z() + ")");
		
		}	
	}
	class StimulusLoop extends TRN4JAVA.Api.Loop
	{
		@Override
		public void callback(final float[] predicted_stimulus) 
		{
		}
	}
}
