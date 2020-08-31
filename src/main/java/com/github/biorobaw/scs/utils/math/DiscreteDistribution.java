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


}
