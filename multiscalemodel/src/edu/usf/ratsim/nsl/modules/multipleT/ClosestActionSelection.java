package edu.usf.ratsim.nsl.modules.multipleT;

import com.vividsolutions.jts.geom.Coordinate;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.vector.PointPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class ClosestActionSelection extends Module {
	int numActions;	
	
	float stepSize;
	//Point3f[] actions; 
	double deltaAngle;
	
	Int0dPort action = new Int0dPort(this);

	public ClosestActionSelection(String name,int numActions) {
		super(name);

		this.numActions = numActions;
		this.deltaAngle = 2*Math.PI/numActions;
		this.addOutPort("action", action);

	}

	
	public void run() {
		
		Coordinate pos = ((PointPort)getInPort("position")).get();
		Coordinate nextPos = ((PointPort)getInPort("nextPosition")).get();
		
		double theta = Math.atan2(nextPos.y-pos.y,nextPos.x-pos.x );
		if (theta < 0) theta+=(2*Math.PI);
		action.set((int)(Math.round(theta/deltaAngle) % numActions));
		

	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
