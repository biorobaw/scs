package platform.simulatorVirtual.components;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.componentInterfaces.DifferentialNavigationInterface;
import edu.usf.experiment.utils.ElementWrapper;

public class DifferentialNavigation implements DifferentialNavigationInterface {

	float left,right;
	
	float maxSpeed;
	float wheelSeparation;
	
	//I assume center of robot coincides with the center of the differential navigation
	
	//precalculated values:
	float deltaV;
	float deltaW;
	float v;
	float w;
	float r;
	boolean isGoingStraight;
	
	
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
		deltaV = right - left;
		isGoingStraight = Math.abs(deltaV) < 0.001;
		v = (right + left)/2;
		
		if(!isGoingStraight){
			
			w = deltaV / ((float)Math.PI * wheelSeparation);
			r = v/w;
		}

	}
	
	public void setPolarSpeeds(float w,float v){
		isGoingStraight = Math.abs(w) < 0.01;
		this.v = v;
		if(isGoingStraight){
			right = left = v;
		} else {
			this.w = w;
			r = v/w;
			right = r - wheelSeparation/2;
			left = r +wheelSeparation/2;
		}
		
	}
	
	public float[] getDisplacement(float deltaT){

		if (isGoingStraight)
			return new float[] {0,v * deltaT,0};
		else
			return new float[] {r*(float)Math.cos(deltaW) - r,
								r*(float)Math.sin(deltaW),
								w*deltaT};
			
	}
	

}
