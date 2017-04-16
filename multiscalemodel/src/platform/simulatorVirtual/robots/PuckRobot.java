package platform.simulatorVirtual.robots;

import java.util.AbstractCollection;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotAction;
import edu.usf.experiment.robot.componentInterfaces.FeederVisibilityInterface;
import edu.usf.experiment.robot.componentInterfaces.LocalizationInterface;
import edu.usf.experiment.robot.specificActions.DifferentialNavigationAction;
import edu.usf.experiment.robot.specificActions.DifferentialNavigationPolarAction;
import edu.usf.experiment.robot.specificActions.FeederTaxicAction;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.GeomUtils;
import edu.usf.ratsim.experiment.universe.virtual.VirtUniverse;
import platform.simulatorVirtual.components.DifferentialNavigation;
import platform.simulatorVirtual.components.FeederVisibility;
import platform.simulatorVirtual.components.GlobalLocalization;

public class PuckRobot extends Robot implements LocalizationInterface , FeederVisibilityInterface {
	
	GlobalLocalization localization = new GlobalLocalization();
	DifferentialNavigation navigation;
	FeederVisibility feederVisibility;
	
	RobotAction currentAction = null;
	VirtUniverse universe;
	
	
	public PuckRobot(ElementWrapper params, Universe univ){
		
		navigation = new DifferentialNavigation(params.getChild("navigation"));
		feederVisibility = new FeederVisibility(params.getChild("feederVisibility"));
		universe = (VirtUniverse)univ;
		
	}
	
	
	@Override
	public void executeTimeStep(float deltaT) {
		
		//clear last state variables
		Subject.instance.clearTriedToEAt();
		Subject.instance.setHasEaten(false);
		
		
		//execute new actions
		
		
		if(currentAction instanceof DifferentialNavigationPolarAction ||
			currentAction instanceof DifferentialNavigationAction){
			
			
			
		} else if (currentAction instanceof FeederTaxicAction) {
			
			FeederTaxicAction a = (FeederTaxicAction)currentAction;
			
			int destinyId = a.id();
			//System.out.println("Destiny feeder: " + a.id());
			if(destinyId==-1){
				System.out.println("ERROR: CANT FIND A NEW FEEDER TO GO TO");
				System.exit(0);
				
				moveRobot(navigation.getDisplacement(deltaT));
				
			}else{
				
				Point3f feederPos = universe.getFeeder(destinyId).getPosition();	
				//System.out.println("feederPos "+feederPos);
				
				float hd = getHD();
				Point3f pos = getPosition();
				
				
				//System.out.println("pos + hd "+pos + " " + hd);
				
				float angleToFeeder = (float)Math.atan2(feederPos.y - pos.y, feederPos.x - pos.x);
				angleToFeeder = GeomUtils.relativeAngle(angleToFeeder, hd);
				
				
				if(Math.abs(angleToFeeder) > 8f/180 * Math.PI){
					//must spin towards goal
					if(angleToFeeder < 0)
						//must spin right
						navigation.setSpeeds(0.1f, -0.1f);
					else
						navigation.setSpeeds(-0.1f, 0.1f);
					
					moveRobot(navigation.getDisplacement(deltaT));
					
				}else{
					navigation.setSpeeds(0.5f, 0.5f);
					
					moveRobot(navigation.getDisplacement(deltaT));
					
					if(tryToEatIfCloseToFeeder(destinyId)){
						navigation.setSpeeds(0, 0);
						//if I successfully eat remove food from feeder -- this logic should be inside the feeder
						universe.setEnableFeeder(destinyId, false);
					}
					
					
					
				}
				
			}
			
		}
		
	}
	
	
	public boolean tryToEatIfCloseToFeeder(int feederId){
		
		if(universe.isRobotCloseToFeeder(feederId) ){
			
			Subject.instance.setTriedToEat();
			Boolean ate = universe.robotEat();
			if(ate) Subject.instance.setHasEaten(ate); //set only if ate==true just in case this function is called more than once
			System.out.println("Try to eat from: "+feederId +" "+ate);

			return true;
		}
		
		return false;

	}
	
	public void moveRobot(float[] displacement){

		universe.moveRobot(new Vector3f(displacement[1],displacement[0],0));
		universe.rotateRobot(displacement[2]);
		
		
	}
	
	public Point3f getPosition(){
		return localization.getPosition();
	}
	
	@Override
	public float getHD() {
		return localization.getHD();
	}


	@Override
	public void processAction(RobotAction action) {
		if(action instanceof DifferentialNavigationAction){
			DifferentialNavigationAction a = (DifferentialNavigationAction)action;
			navigation.setSpeeds(a.left(), a.right());
			
			currentAction = a;
			
		}else if (action instanceof DifferentialNavigationPolarAction){
			DifferentialNavigationPolarAction a = (DifferentialNavigationPolarAction)action;
			navigation.setSpeeds(a.v(), a.w());
			
			currentAction = a;
			
		} else if (action instanceof FeederTaxicAction){
			currentAction = action;
		}
		
	}


	@Override
	public List<Feeder> getVisibleFeeders(int[] except) {
		return feederVisibility.getVisibleFeeders(except);
	}


	@Override
	public Feeder getClosestFeeder() {
		return feederVisibility.getClosestFeeder();
	}


	@Override
	public int getClosestFeeder(AbstractCollection<Integer> feederSet) {
		// TODO Auto-generated method stub
		return feederVisibility.getClosestFeeder(feederSet);
	}


	



	

}
