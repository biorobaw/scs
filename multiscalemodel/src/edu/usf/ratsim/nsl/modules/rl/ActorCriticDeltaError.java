package edu.usf.ratsim.nsl.modules.rl;

import java.util.List;
import java.util.Random;

import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3f;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
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
 * Module that computes the delta for actor critic modules
 * @author biorob
 * 
 */
public class ActorCriticDeltaError extends Module {
	
	Float0dPort delta = new Float0dPort(this);
	
	float gamma; //discountFactor	

	/**
	 * The index of the value in the q table
	 */
	private int valueIndex;

	public ActorCriticDeltaError(String name,float discountFactor, int valueIndex) {
		super(name);
		gamma = discountFactor;
		this.addOutPort("delta", delta);
		this.valueIndex = valueIndex;
	}

	
	public void run() {
		float r = ((Float0dPort)getInPort("reward")).get();
		float[] oldQ = ((Float1dPort)getInPort("copyQ")).getData();
		float[] Q = ((Float1dPortArray)getInPort("Q")).getData();
		
		delta.set(r + gamma*Q[valueIndex] - oldQ[valueIndex]);			
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
