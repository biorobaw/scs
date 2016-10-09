package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.List;
import java.util.Random;

import javax.lang.model.element.ExecutableElement;
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
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class CurrentActionBiasModule extends Module {
	int numActions;	
	
	float[] probabilities;
	float stepSize;
	float bias;
	
	Runnable run;	
	

	public CurrentActionBiasModule(String name,int numActions,float bias) {
		super(name);

		this.numActions = numActions;
		probabilities = new float[numActions];
		this.bias = bias;
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		run = new RunFirstTime();

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		run.run();
	}

	
	
	public class RunGeneral implements Runnable {
		public void run() {
			Float1dPortArray input = (Float1dPortArray) getInPort("input");
			int action = ((Int0dPort) getInPort("action")).get();
			
			for (int i =0;i<numActions;i++)
				probabilities[i] = (1-bias)*input.get(i);

			probabilities[action]+=bias;
			
			
		}
	}
		
	public class RunFirstTime implements Runnable{
		public void run(){
			
			Float1dPortArray input = (Float1dPortArray) getInPort("input");
			
			for (int i =0;i<numActions;i++)
				probabilities[i] = input.get(i);
			
			run = new RunGeneral();
			
		}
		
		
		
	}
	


	@Override
	public boolean usesRandom() {
		return false;
	}



	
}
