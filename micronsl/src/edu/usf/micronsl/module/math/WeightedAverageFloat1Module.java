package edu.usf.micronsl.module.math;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.Port;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.sum.Float1dPortSum;
import edu.usf.micronsl.port.singlevalue.Float0dPort;

public class WeightedAverageFloat1Module extends Module {


	Float0dPort weight  = new Float0dPort(this,0);
	Float1dPortArray average;
	float[] averageValues;
	
	ArrayList<Float1dPort> values   = new ArrayList<>();
	ArrayList<Float0dPort> weights  = new ArrayList<>();
	
	public WeightedAverageFloat1Module(String name, int vectorSize) {
		super(name);
		
		averageValues = new float[vectorSize];
		average = new Float1dPortArray(this, averageValues);
		
		addOutPort("average", average);
		addOutPort("weight", weight);
	}
	
	public Float1dPortArray getAveragePort() {
		return average;
	}

	
	
	
	
	public void addInPorts(List<Port> values,List<Port> weights) {
		super.addInPorts(values);
		super.addInPorts(weights);
		values.forEach(v -> this.values.add((Float1dPort)v));
		weights.forEach(v -> this.weights.add((Float0dPort)v));
	}
	
	public void addInPort(Port value,Port weight) {
		super.addInPort(value);
		super.addInPort(weight);
		this.values.add((Float1dPort)value);
		this.weights.add((Float0dPort)weight);
	}


	public void run() {
		double weightSum = 0;
		for(int i=0;i<averageValues.length;i++) averageValues[i]=0;
		
		for(int i=0;i<weights.size();i++) {
			float w = weights.get(i).get();
			float[] v = values.get(i).getData();
			
			for(int j=0; j<averageValues.length;j++) averageValues[j]+= w*v[j];
			weightSum += w;
		}
		
		weight.set((float)weightSum);
		for(int i=0; i<averageValues.length;i++)
			averageValues[i] = weightSum == 0 ? 0 : (float)(averageValues[i]/weightSum);		
		
		
	}

	@Override
	public boolean usesRandom() {
		return false;
	}


}
