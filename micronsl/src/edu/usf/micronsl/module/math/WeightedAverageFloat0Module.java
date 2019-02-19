package edu.usf.micronsl.module.math;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.sum.Float1dPortSum;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

public class WeightedAverageFloat0Module extends Module {


	Float0dPort average = new Float0dPort(this,0);
	Float0dPort weight  = new Float0dPort(this,0);
	
	ArrayList<Float0dPort> values   = new ArrayList<>();
	ArrayList<Float0dPort> weights  = new ArrayList<>();
	
	public WeightedAverageFloat0Module(String name) {
		super(name);
		addOutPort("average", average);
		addOutPort("weight", weight);
	}

	
	
	
	
	public void addInPorts(List<Port> values,List<Port> weights) {
		super.addInPorts(values);
		super.addInPorts(weights);
		values.forEach(v -> this.values.add((Float0dPort)v));
		weights.forEach(v -> this.weights.add((Float0dPort)v));
	}
	
	public void addInPort(Port value,Port weight) {
		super.addInPort(value);
		super.addInPort(weight);
		this.values.add((Float0dPort)value);
		this.weights.add((Float0dPort)weight);
	}


	public void run() {
		double weightSum = 0;
		double valuSum 	 = 0;
		
		for(int i=0;i<weights.size();i++) {
			float w = weights.get(i).get();
			float v = values.get(i).get();
			
			valuSum   += w*v;
			weightSum += w;
		}
		
		weight.set((float)weightSum);
		average.set(weightSum == 0 ? 0 : (float)(valuSum/weightSum));
			
		
		
	}
	
	public Float0dPort getAveragePort() {
		return average;
	}
	
	public Float0dPort getWeightPort() {
		return weight;
	}

	@Override
	public boolean usesRandom() {
		return false;
	}


}
