package edu.usf.ratsim.nsl.modules.multipleT;

import edu.usf.micronsl.module.Module;
import edu.usf.micronsl.port.onedimensional.sparse.Float1dSparsePort;
import edu.usf.micronsl.port.singlevalue.Float0dPort;
import edu.usf.micronsl.port.singlevalue.Int0dPort;
import edu.usf.micronsl.port.twodimensional.FloatMatrixPort;

/**
 * Module that sets the probability of an action to 0 if the action can not be performed
 * It assumes actions are allocentric,  distributed uniformly in the range [0,2pi]
 * @author biorob
 * 
 */
public class UpdateQModule extends Module {
	
	
	private float nu; //learning rate
	private int numActions;
		
	float[][] dotProducts;
	

	public UpdateQModule(String name,int numActions,float nu) {
		super(name);

		this.numActions = numActions;
		this.nu = nu;
		dotProducts = new float[numActions][numActions];
		
		double deltaAngle = 2*Math.PI/numActions;
		float anglei = 0;
		for (int i=0;i<numActions;i++)	
		{	
			float x = (float)Math.cos(anglei);
			float y = (float)Math.sin(anglei);
			
			float anglej = 0;
			for(int j=0;j<numActions;j++)
			{
				dotProducts[i][j] = Math.max(x*(float)Math.cos(anglej) + y*(float)Math.sin(anglej),0);
				//dotProducts[i][j] *=(dotProducts[i][j]*dotProducts[i][j]);
				dotProducts[i][j] = i==j ? 1 : 0;
				//System.out.print("\t"+dotProducts[i][j]);
				anglej+=deltaAngle;
			}
			//System.out.println("");
			anglei+=deltaAngle;
		}
		
		//System.out.println("dotProduct "+dotProducts);
		

	}

	
	public void run() {
		float nuDelta = nu*((Float0dPort)getInPort("delta")).get();
		int action = ((Int0dPort)getInPort("action")).get();
		FloatMatrixPort Q = (FloatMatrixPort)getInPort("Q");
		Float1dSparsePort PCs = (Float1dSparsePort)getInPort("placeCells");
		
		if (nuDelta==0) return;
		for (int i : PCs.getNonZero().keySet())
			for (int j =0;j<numActions;j++)
			{
				Q.set(i,j, Q.get(i,j) + nuDelta*PCs.get(i)*dotProducts[j][action]);
				
				//if(nuDelta!=0) System.out.println("i,j,Q:"+i+","+j+","+Q.get(i, j));
				
			}

		
			
		
	}


	@Override
	public boolean usesRandom() {
		return false;
	}
}
