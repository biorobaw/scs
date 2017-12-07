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
import edu.usf.experiment.robot.specificActions.MoveToAction;
import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.subject.SubjectOld;
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
	
	
	Float closeEnoughToTargetPosition = 0.02f*0.02f/2; //distance squared
	
	
	public PuckRobot(ElementWrapper params, Universe univ){
		
		navigation = new DifferentialNavigation(params.getChild("navigation"));
		feederVisibility = new FeederVisibility(params.getChild("feederVisibility"));
		universe = (VirtUniverse)univ;
		
	}
	
	
	@Override
	public void executeTimeStep(float deltaT) {
		
		//clear last state variables
		Subject.instance.clearCycleState();
		
		
		
		//execute new actions
		
		
		if(currentAction instanceof DifferentialNavigationPolarAction ||
			currentAction instanceof DifferentialNavigationAction){
			
			// speeds are already set so do nothing
			
		} else if (currentAction instanceof FeederTaxicAction) {
			
			FeederTaxicAction a = (FeederTaxicAction)currentAction;
			
			int destinyId = a.id();
			//System.out.println("Destiny feeder: " + a.id());
			if(destinyId==-1){
				System.out.println("ERROR: CANT FIND A NEW FEEDER TO GO TO");
				System.exit(0);
				
				moveRobot(navigation.getDisplacement(deltaT));
				currentAction = null;
				
			}else{
				
				System.out.println("Destiny: " + " " + destinyId + " " + universe.getFeeder(destinyId));
				Point3f feederPos = universe.getFeeder(destinyId).getPosition();	
				//System.out.println("feederPos "+feederPos);
				
				if( navigateToCoordinate(feederPos, deltaT,closeEnoughToTargetPosition) ){
					//if goeal reached, try to eat from feeder
					
					if(tryToEatIfCloseToFeeder(destinyId)){
						//if I successfully eat remove food from feeder -- this logic should be inside the feeder
//						universe.setEnableFeeder(destinyId, false);
						universe.setActiveFeeder(destinyId, false);
					}
					
					//signal action completion
					actionMessageBoard.put(currentAction.actionId, "done");
					currentAction = null;
					
				};
				
				
			}
			
		} else if (currentAction instanceof MoveToAction ) {
			MoveToAction a = (MoveToAction)currentAction;
			
			//System.out.println("RESERVOIR TARGET COORDINATES (" + a.x() +", "+ a.y() +", " + a.z() + ")");
			if(navigateToCoordinate(new Point3f(a.x(),a.y(),a.z()), deltaT, closeEnoughToTargetPosition)){
				
				actionMessageBoard.put(currentAction.actionId,"done");
				currentAction = null;
				
			}
			
			
		}
		
	}
	
	public boolean navigateToCoordinate(Point3f targetPos, float deltaT,float errorTolerance){
		
		Vector3f deltaV = GeomUtils.deltaVector(getPosition(), targetPos);
		
		//check if already on target position
		if( deltaV.lengthSquared() < errorTolerance ) {
			// already in target position
			navigation.setSpeeds(0, 0);
			return true;
		}
		
		
		//check if pointing towards goal
		float hd = getHD();		
		float angleToTarget = (float)Math.atan2(deltaV.y, deltaV.x);
		angleToTarget = GeomUtils.relativeAngle(angleToTarget, hd);
		
		
		if(Math.abs(angleToTarget) > 8f/180 * Math.PI){
			//must spin towards goal
			if(angleToTarget < 0)
				//must spin right
				navigation.setSpeeds(0.1f, -0.1f);
			else
				navigation.setSpeeds(-0.1f, 0.1f);
			
			moveRobot(navigation.getDisplacement(deltaT));
			
			return false; //still in same location so target not reached yet
			
		}else{
			navigation.setSpeeds(0.5f, 0.5f);
			
			moveRobot(navigation.getDisplacement(deltaT));
			
			//check if reached target
			if(GeomUtils.deltaVector(getPosition(), targetPos).lengthSquared() < errorTolerance){
				navigation.setSpeeds(0, 0);
				return true;
				
			} else return false;
			
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
			navigation.setPolarSpeeds( a.w(),a.v());
			
			currentAction = a;
			
		} else if (action instanceof FeederTaxicAction){
			currentAction = action;
		} else if (action instanceof MoveToAction){
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
