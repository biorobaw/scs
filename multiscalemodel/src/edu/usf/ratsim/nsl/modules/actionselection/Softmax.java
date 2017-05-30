package edu.usf.ratsim.nsl.modules.actionselection;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;

/**
 * Module to generate random actions when the agent hasnt moved (just rotated)
 * for a while
 * 
 * @author biorob
 * 
 */
public class Softmax extends Module {
	public float[] probabilities;
	int numActions;
	

	public Softmax(String name,int numActions) {
		super(name);

		probabilities = new float[numActions];
		this.numActions = numActions;
		addOutPort("probabilities", new Float1dPortArray(this, probabilities));

	}

	public void run() {
		Float1dPort input = (Float1dPort) getInPort("input");

		float max = -Float.MAX_VALUE;
		for (int i =0; i < numActions; i++)
			max = input.get(i) > max ? input.get(i) : max;
			
		float sum = 0;
		for (int i=0;i<numActions;i++){
			// Use the max to normalize - this takes care of too high values and also too low values
//			probabilities[i] = (float)Math.exp(input.get(i) - max);
			probabilities[i] = (float)Math.exp(input.get(i));
			sum+=probabilities[i];
		}
		//System.out.print("\nsum: "+sum+"\nP: ");
		
//		System.out.print("Softmax output: ");
		if (sum==Float.POSITIVE_INFINITY) 
			throw new IllegalArgumentException("Argument 'divisor' is Infinity");
		if (sum==0) 
			throw new IllegalArgumentException("Argument 'divisor' is 0");
		for (int i=0;i<numActions;i++){
			probabilities[i]/=sum;
		}


	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
