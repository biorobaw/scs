package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.experiment.utils.GeomUtils;
import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.array.Float1dPortArray;
import edu.usf.micronsl.port.singlevalue.Int0dPort;

/**
 * Module gives preferences to actions that move the rat forward.
 * I assume uniformly distributed actions and that the rat is pointing in the direction of the action last taken
 * This assumes absolute directions. To remove absolute directions the value shift must be set to 0
 * @author biorob
 * 
 */
public class Last2ActionsActionGating extends Module {
	int numActions;	
	
	public float[] probabilities;	
	float[][][] precalculatedValues;
	
	public float[] chosenRing; //stores the chosen bias ring so it can be visualized - the ring is chosen from the precalculated values
	
	
	float exponent;
	float uniformProbability;
	
	int last2Action;
	
	public Runnable run;	
	

	public Last2ActionsActionGating(String name,int numActions,float exponent,float uniformProbability) {
		super(name);

		this.numActions = numActions;
		probabilities = new float[numActions];
		precalculatedValues = new float[numActions][numActions][numActions];
		chosenRing = new float[numActions];
		this.addOutPort("probabilities", new Float1dPortArray(this, probabilities));
		run = new RunFirstTime();
		
		this.exponent = exponent;
		this.uniformProbability = uniformProbability;
		
		float base = uniformProbability/numActions;
		float complement = 1-uniformProbability;
		
		
		float dTheta = 2*(float)Math.PI/numActions;
		float theta = 0;

		//primer indice es ultima accion tomada
		//segundo indice es accion anterior tomada
		//tercer indice es peso de la posible accion a tomar
		for(int i=0;i<numActions;i++){
			
			float theta2 = 0;
			for(int j=0;j<numActions;j++){
				
				float sum=0;
				
				float relAngle =GeomUtils.relativeAngle(theta2, theta);
				if ( Math.abs(relAngle) > Math.PI*(91.0/180.0) ){
					//En este caso se ignora accion anterior a la ultima:
					float ang = theta; 
					
					float theta3 = 0;
					
					for(int k=0;k<numActions;k++){
						
						float rel = Math.abs(GeomUtils.relativeAngle(theta3, ang));
						if(rel > Math.PI*(91.0/180.0)) precalculatedValues[i][j][k]=0;
						else {
							precalculatedValues[i][j][k] = (float)Math.pow(1-rel/(float)Math.PI,exponent);
							sum+=precalculatedValues[i][j][k];
						}
						
						theta3+=dTheta;
					}
					
				}else{
					float ang = theta + relAngle/2;
					
					float theta3 = 0;
					
					for(int k=0;k<numActions;k++){
						
						//System.out.println(""+i+","+j+","+k+": ");
//						System.out.println("ang "+ang+", theta3 "+theta3);
//						System.out.println();
						float rel = Math.abs(GeomUtils.relativeAngle(theta3, ang));
						float rel2= Math.abs(GeomUtils.relativeAngle(theta3, theta));
						float rel3= Math.abs(GeomUtils.relativeAngle(theta3, theta2));
//						System.out.println("r: "+rel+", "+rel2+", "+rel3);
						if(rel2 > Math.PI*(91.0/180.0) || rel3 > Math.PI*(91.0/180.0) ) precalculatedValues[i][j][k]=0;
						else {
							precalculatedValues[i][j][k] = (float)Math.pow(1-rel/(float)Math.PI,exponent);
							sum+=precalculatedValues[i][j][k];
						}
						
						theta3+=dTheta;
					}
					
				}
				
				for (int k=0;k<numActions;k++)
					precalculatedValues[i][j][k] = base + complement*precalculatedValues[i][j][k]/sum;
				
				theta2+=dTheta;
			}
			
			
			theta += dTheta;
		}
		
		
		
		//System.out.println("PRECALCULATED Values");
		//for (int i =0;i<numActions;i++)
			//System.out.println("Bias "+i+" "+precalculatedValues[i]);
//		System.out.println("PRECALCULATED Values");
//		for (int i =0;i<numActions;i++)
//			for(int j =0;j<numActions;j++){
//				System.out.print(""+i+","+j+":  ");
//				for(int k=0;k<numActions;k++)
//					System.out.print(""+precalculatedValues[i][j][k]+";");
//				System.out.println("");
//			}
//		
		

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
							
			float sum = 0;
			for (int i =0;i<numActions;i++){
				probabilities[i] = precalculatedValues[action][last2Action][i]*input.get(i);
				chosenRing[i] = precalculatedValues[action][last2Action][i];
				sum+=probabilities[i];
			}
			//System.out.println("previous: "+action);
			//System.out.print("Probabilities: (");
			for (int i = 0; i < numActions; i++) {
				probabilities[i]/=sum;
				
				//System.out.print(""+probabilities[i]+" ");
			}
			//System.out.println(")");
			
			last2Action = action;
			
		}
	}
	
	
	public class RunSecondTime implements Runnable {
		public void run() {
			Float1dPortArray input = (Float1dPortArray) getInPort("input");//probability vector
			int action = ((Int0dPort) getInPort("action")).get();
				
			float sum = 0;
			for (int i =0;i<numActions;i++){
				probabilities[i] = precalculatedValues[action][action][i]*input.get(i);
				chosenRing[i] = precalculatedValues[action][action][i];
				sum+=probabilities[i];
			}
			//System.out.println("previous: "+action);
			//System.out.print("Probabilities: (");
			for (int i = 0; i < numActions; i++) {
				probabilities[i]/=sum;
				//System.out.print(""+probabilities[i]+" ");
			}
			//System.out.println(")");
			
			
			last2Action = action;
			
			run = new RunGeneral();
		}
	}
		
	public class RunFirstTime implements Runnable{
		public void run(){
			
			Float1dPortArray input = (Float1dPortArray) getInPort("input");//probability vector
			
			for (int i =0;i<numActions;i++){
				probabilities[i] = input.get(i);
				chosenRing[i] = (float)1.0/numActions;
			}
			
			run = new RunSecondTime();
			
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
