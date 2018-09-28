package edu.usf.ratsim.model.morris_replay.submodules;



import java.util.List;

import edu.usf.experiment.robot.AbsoluteDirectionRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.affordance.Affordance;
import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.vlwsim.robot.AbsoluteDirectionVirtualRobot;
import edu.usf.vlwsim.robot.VirtualRobot;
import edu.usf.vlwsim.universe.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class DistancesInputModule extends Module {
	
	public float[] distances;
	public float[] angles;
	float maxDistance;

	private VirtualRobot robot;

	
	public DistancesInputModule(String name,VirtualRobot robot, int numActions,float maxDistance){
		super(name);
		angles = new float[numActions];
		for(int i= 0; i<numActions ; i++) angles[i]= (float)(2*Math.PI/numActions*i);
		distances =new float[angles.length];
		
		this.addOutPort("distances", new Float1dPortArray(this, distances));
		this.robot = robot;
		this.maxDistance = maxDistance;
		
	}
	
	public DistancesInputModule(String name,VirtualRobot robot, float[] angles,float maxDistance) {
		super(name);
		this.angles = angles;
		distances =new float[angles.length];
		
		this.addOutPort("distances", new Float1dPortArray(this, distances));
		this.robot = robot;
		this.maxDistance = maxDistance;
	}

	
	public void run() {
		for(int i=0;i<angles.length;i++) distances[i] = robot.getAbsoluteDistance(angles[i],maxDistance);
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
