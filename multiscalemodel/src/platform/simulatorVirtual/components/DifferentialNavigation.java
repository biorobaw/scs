package platform.simulatorVirtual.components;

import edu.usf.experiment.robot.componentInterfaces.DifferentialNavigationInterface;
import edu.usf.experiment.utils.ElementWrapper;

public class DifferentialNavigation implements DifferentialNavigationInterface {

	float left,right;
	
	float maxSpeed;
	float wheelSeparation;
	
	//I assume center of robot coincides with the center of the differential navigation
	
	//precalculated values:
	public float deltaV;
	public float v;
	public float w;
	public float r;
	public boolean isGoingStraight;
	
	
	public DifferentialNavigation(ElementWrapper params) {
		// TODO Auto-generated constructor stub
		wheelSeparation = params.getChildFloat("separation");
		maxSpeed = params.getChildFloat("maxSpeed");
	}
	
	@Override
	public void setSpeeds(float left, float right) {
		// TODO Auto-generated method stub
		this.left = maxSpeed*left;
		this.right = maxSpeed*right;
		
		//precalculate values to save time each iteration
		deltaV = this.right - this.left;
		isGoingStraight = Math.abs(deltaV) < 0.001;
		v = (this.right + this.left)/2;
		w=0;
		r=0;
		
		if(!isGoingStraight){
			
			w = 2 * deltaV /  wheelSeparation;
			r = v/w;
		}

	}
	
	public void setPolarSpeeds(float w,float v){
		isGoingStraight = Math.abs(w) < 0.01;
		this.v = v*maxSpeed;
		if(isGoingStraight){
			right = left = this.v;
		} else {
			this.w = w;
			r = this.v/w;
			right = r - w*wheelSeparation/4;
			left = r +w*wheelSeparation/4;
		}
		
	}
	
	public float[] getDisplacement(float deltaT){

		//System.out.println("W "+ w);
		if (isGoingStraight)
			return new float[] {0,v * deltaT,0};
		else
			return new float[] {r*(float)Math.cos(w*deltaT) - r,
								r*(float)Math.sin(w*deltaT),
								w*deltaT};
			
	}
	

}
