package edu.usf.ratsim.nsl.modules.multipleT;

import java.util.List;
import java.util.Random;

import javax.lang.model.element.ExecutableElement;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3f;

import com.sun.xml.internal.ws.api.Cancelable;

import edu.usf.experiment.robot.LocalizableRobot;
import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.subject.affordance.EatAffordance;
import edu.usf.experiment.subject.affordance.ForwardAffordance;
import edu.usf.experiment.utils.Debug;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.experiment.utils.RandomSingleton;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.onedimensional.vector.Point3fPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;

/**
 * Module gives preferences to actions that move the rat forward.
 * I assume uniformly distributed actions and that the rat is pointing in the direction of the action last taken
 * This assumes absolute directions. To remove absolute directions the value shift must be set to 0
 * @author biorob
 * 
 */
public class DontGoBackBiasModule extends Module {
	int numActions;	
	
	public float[] probabilities;	
	float[] precalculatedValues;
	
	
	float exponent;
	float uniformProbability;
	
	public Runnable run;	
	

	public DontGoBackBiasModule(String name,int numActions,float exponent,float uniformProbability) {
		super(name);

		this.numActions = numActions;
		probabilities = new float[numActions];
		precalculatedValues = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		run = new RunFirstTime();
		
		this.exponent = exponent;
		this.uniformProbability = uniformProbability;
		
		float base = uniformProbability/numActions;
		float complement = 1-uniformProbability;
		
		
		
		float theta = 0;
		float dTheta = 2*(float)Math.PI/numActions;
		float sum=0;
		for(int i=0;i<numActions;i++){
			float absTheta = 1-Math.abs(GeomUtils.relativeAngle(theta, 0))/(float)Math.PI; //gives angle between -pi,pi
			
			precalculatedValues[i] = (float)Math.pow(absTheta,exponent);
			sum+=precalculatedValues[i];
			
			theta += dTheta;
		}
		
		for (int i=0;i<numActions;i++)
			precalculatedValues[i] = base + complement*precalculatedValues[i]/sum;
		
		//System.out.println("PRECALCULATED Values");
		//for (int i =0;i<numActions;i++)
			//System.out.println("Bias "+i+" "+precalculatedValues[i]);
		
		
		

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
			
			int shift = numActions - action;
			
			float sum = 0;
			for (int i =0;i<numActions;i++){
				probabilities[i] = precalculatedValues[(shift+i)%numActions]*input.get(i);
				sum+=probabilities[i];
			}
			//System.out.println("previous: "+action);
			//System.out.print("Probabilities: (");
			for (int i = 0; i < numActions; i++) {
				probabilities[i]/=sum;
				//System.out.print(""+probabilities[i]+" ");
			}
			//System.out.println(")");
			
			
			
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
	
	public void newEpisode(){
		run = new RunFirstTime();
	}
	
	public void newTrial(){
		run = new RunFirstTime();
	}
	


	@Override
	public boolean usesRandom() {
		return false;
	}



	
}
