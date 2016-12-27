package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.LinkedList;

import javax.vecmath.Point3f;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class NextPositionModule extends Module {	

	LinkedList<Point3f> pcCenters;
	Int0dPort nextActive;
	Point3fPort nextPosition = new Point3fPort(this);
	
	public NextPositionModule(String name,LinkedList<Point3f> pcCenters) {
		super(name);
		this.pcCenters = pcCenters;
		this.addOutPort("nextPosition", nextPosition);

	}

	
	public void run() {
		int nextActive = ((Int0dPort)getInPort("nextActive")).get();		
		nextPosition.set(pcCenters.get(nextActive));
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
