package edu.usf.ratsim.nsl.modules.rl;

import java.util.List;
import java.util.Random;

import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.Float1dPort;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class SarsaQDeltaError extends Module {
	
	Float0dPort delta = new Float0dPort(this);
	
	float gamma; //discountFactor	

	public SarsaQDeltaError(String name,float discountFactor) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float[] oldQ = ((Float1dPort)getInPort("copyQ")).getData();
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		int oldAction = ((Int0dPort)getInPort("copyAction")).get();
		int action = ((Int0dPort)getInPort("action")).get();
		
//		if(r>0) {
//			System.out.println("reward: "+r);
//			new java.util.Scanner(System.in).nextLine();
//		}
		delta.set(r + gamma*Q[action] - oldQ[oldAction]);			
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
