package platform.simulatorVirtual.robots;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotAction;
import edu.usf.experiment.robot.robotInterfaces.Localizable;
import edu.usf.experiment.robot.specificActions.DifferentialNavigationAction;
import edu.usf.experiment.robot.specificActions.DifferentialNavigationPolarAction;
import edu.usf.experiment.utils.ElementWrapper;
import platform.simulatorVirtual.components.DifferentialNavigation;
import platform.simulatorVirtual.components.GlobalLocalization;

public class Puck extends Robot implements Localizable{
	
	DifferentialNavigation navigation;
	GlobalLocalization localization = new GlobalLocalization();
	RobotAction currentAction = null;
	
	public Puck(ElementWrapper params){
		
		navigation = new DifferentialNavigation(params.getChild("navigation"));
		
	}
	
	
	public Point3f getPosition(){
		return localization.getPosition();
	}


	@Override
	public void processAction(RobotAction action) {
		if(action instanceof DifferentialNavigationAction){
			DifferentialNavigationAction a = (DifferentialNavigationAction)action;
			navigation.setSpeeds(a.left(), a.right());
			
		}else if (action instanceof DifferentialNavigationPolarAction){
			DifferentialNavigationPolarAction a = (DifferentialNavigationPolarAction)action;
			navigation.setSpeeds(a.v(), a.w());
		}
		
	}


	@Override
	public void executeTimeStep(float deltaT) {
		// TODO Auto-generated method stub
		
	}



	

}
