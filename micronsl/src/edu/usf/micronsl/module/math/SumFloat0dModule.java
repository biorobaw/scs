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

public class SumFloat0dModule extends Module {


	Float0dPort sum = new Float0dPort(this,0);
	
	ArrayList<Float0dPort> values   = new ArrayList<>();
	
	public SumFloat0dModule(String name) {
		super(name);
		addOutPort("average", sum);
	}

	
	public void addInPorts(List<Port> values) {
		super.addInPorts(values);
		values.forEach(v -> this.values.add((Float0dPort)v));
	}
	
	public void addInPort(Port value) {
		super.addInPort(value);
		this.values.add((Float0dPort)value);
	}


	public void run() {
		float valuSum 	 = 0;
		
		for(int i=0;i<values.size();i++) {
			valuSum   += values.get(i).get();
		}
		
		sum.set(valuSum);
			
		
		
	}
	
	public Float0dPort getSumPort() {
		return sum;
	}
	


	@Override
	public boolean usesRandom() {
		return false;
	}


}
