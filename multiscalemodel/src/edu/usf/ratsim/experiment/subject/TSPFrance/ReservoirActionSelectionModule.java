package edu.usf.ratsim.experiment.subject.TSPFrance;

import java.util.LinkedList;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.Globals;
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
	
	boolean firstCycle = true;

	public ReservoirActionSelectionModule(String name, Reservoir reservoir) {
		super(name);
		this.reservoir = reservoir;
 		reservoir.finishInitialization(new Agent());
		// TODO Auto-generated constructor stub
	}

	int positions = 0;
	@Override
	public void run()
	{
		Bool0dPort finishedAction = (Bool0dPort)getInPort("finishedAction");
		
		if (firstCycle || finishedAction.get())
		{
			firstCycle=false;
			
			positions++;
			//system.out.println("Position " + positions + "/75");
			
			Point3f next_position = reservoir.get_next_position();
			if (next_position != null)
			{
				action.setX(next_position.x);
				action.setY(next_position.y);
				//system.out.println(Globals.getInstance().get("cycle")+" moving robot to position "  + next_position.x + ", " + next_position.y);
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
		firstCycle = true;
	}
	
	
	class Agent extends TRN4JAVA.Custom.Simulation.Encoder
	{
		private int counter = 0;
		@Override
		public void callback(final long simulation_id, final long evaluation_id, final float prediction[], final long rows, final long cols)
		{
			assert(rows == 1);
			assert(cols == 2);
			
			System.out.println("generation step counter = " + counter++);
			reservoir.append_next_position(simulation_id, evaluation_id, new Point3f(prediction[0], prediction[1], 0.0f));
		}	
	}

}
