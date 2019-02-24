package edu.usf.ratsim.model.pablo.multiplet.submodules;



import edu.usf.experiment.robot.affordance.AffordanceRobot;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class DistanceAffordanceGatingModule extends Module {
	
	public float[] probabilities;
	public float[] gates;

	private AffordanceRobot robot;
	int numActions ;
	float minDistance;
	
	
	public DistanceAffordanceGatingModule(String name,int numActions,float minDistance) {
		super(name);
		this.numActions = numActions;
		
		this.minDistance = minDistance;
		
		probabilities = new float[numActions];
		gates = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));

	}

	
	public void run() {
		Float1dPortArray input = (Float1dPortArray) getInPort("input");
		Float1dPortArray distances = (Float1dPortArray) getInPort("distances");
		
		
		
		float sum = 0;
		for (int i =0;i<numActions;i++)
		{
//			double distance = universe.distanceToNearestWall(endPoints[i].x,endPoints[i].y, maxDistance);
			if(distances.get(i) <= minDistance) probabilities[i] = gates[i] = 0;
			else{
				//System.out.println("action "+i+" posible");
				probabilities[i] =  (float)(input.get(i)*distances.get(i));
				gates[i] = distances.get(i);
				sum += probabilities[i];
			}
			
		}
		
		if(sum==0) throw new IllegalArgumentException("Argument 'divisor' is 0");
		for (int i =0;i<numActions;i++) probabilities[i]/=sum;
		

	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}