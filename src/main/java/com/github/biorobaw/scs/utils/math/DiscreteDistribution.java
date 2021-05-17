package com.github.biorobaw.scs.utils.math;


import com.github.biorobaw.scs.utils.math.RandomSingleton;

/**
 * 
 * @author biorob
 * 
 */
public class DiscreteDistribution  {	

	static public int sample(float[] dist) {

		float u = RandomSingleton.getInstance().nextFloat();
		int i=1;
		for (float sum = dist[0];  sum <= u && i < dist.length;  i++) {
			sum+=dist[i];
		}
		while(dist[i-1]==0 && i>0) i--; // fixes precision issues where sum(dist) = 1-epsilon < u < 1 
		return i-1;

	}
	
	static public int sample(float[] weights, int length) {

		float total = 0;
		for(int i = 0 ; i < length; i++) total+=weights[i];
		
		float u = RandomSingleton.getInstance().nextFloat()  * total;
		int i=1;
		for (float sum = weights[0];  sum <= u && i < weights.length;  i++) {
			sum+=weights[i];
		}
		while(weights[i-1]==0 && i>0) i--; // fixes precision issues where sum(dist) = 1-epsilon < u < 1 
		return i-1;

	}


}
