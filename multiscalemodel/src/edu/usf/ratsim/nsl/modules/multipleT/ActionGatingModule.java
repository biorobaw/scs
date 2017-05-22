package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.vlwsim.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class ActionGatingModule extends Module {
	int numActions;	
	
	public float[] probabilities;
	float minDistance;
	float maxDistance;

	Point3f[] endPoints;
	

	public ActionGatingModule(String name,int numActions,float minDistance,float maxDistance) {
		super(name);

		this.numActions = numActions;
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		
		probabilities = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		endPoints = new Point3f[numActions];
		
		double deltaAngle = 2*Math.PI/numActions;
		float angle = 0;
		for (int i=0;i<numActions;i++)
		{	
			double dx = Math.cos(angle), dy = Math.sin(angle);
			endPoints[i] = new Point3f((float)dx*maxDistance,(float)dy*maxDistance,0);
			angle+=deltaAngle;
		}

	}

	
	public void run() {
		Float1dPortArray input = (Float1dPortArray) getInPort("input");
		VirtUniverse universe = VirtUniverse.getInstance();
		
		float sum = 0;
		for (int i =0;i<numActions;i++)
		{
			double distance = universe.distanceToNearestWall(endPoints[i].x,endPoints[i].y, maxDistance);
			if(distance <= minDistance) probabilities[i] = 0;
			else{
				//System.out.println("action "+i+" posible");
				probabilities[i] =  (float)(input.get(i)*distance);
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
