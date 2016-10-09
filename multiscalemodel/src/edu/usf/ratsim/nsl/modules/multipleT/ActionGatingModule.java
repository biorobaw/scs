package edu.usf.ratsim.nsl.modules.multipleT;

import javax.vecmath.Point3f;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class ActionGatingModule extends Module {
	int numActions;	
	
	public float[] probabilities;
	float stepSize;
	
	Point3f[] actions; 
	

	public ActionGatingModule(String name,int numActions,float stepSize) {
		super(name);

		this.numActions = numActions;
		probabilities = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		actions = new Point3f[numActions];
		
		double deltaAngle = 2*Math.PI/numActions;
		float angle = 0;
		for (int i=0;i<numActions;i++)
		{	
			actions[i] =new Point3f((float)Math.cos(angle)*stepSize,(float)Math.sin(angle)*stepSize,0);
			angle+=deltaAngle;
		}

	}

	
	public void run() {
		Float1dPortArray input = (Float1dPortArray) getInPort("input");
		VirtUniverse universe = VirtUniverse.getInstance();
		
		float sum = 0;
		for (int i =0;i<numActions;i++)
		{
			probabilities[i] = input.get(i);
			if (!universe.canRobotMoveDeltaPos(actions[i])){
				
			
				probabilities[i]=0;
				//System.out.println("action "+i+" impossible");
			}
			else{
				//System.out.println("action "+i+" posible");
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
