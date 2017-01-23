package edu.usf.experiment.subject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.utils.ElementWrapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class SubjectOld extends Subject{

	private RobotOld robot;
	private boolean triedToEat;
	private boolean rewarded;
	
	
	public SubjectOld(String name, String group, ElementWrapper modelParams, RobotOld robot) {
		this.name = name;
		this.group = group;
		this.robot = robot;
		
		robot.startRobot();
		
		hasEaten = false;
		triedToEat = false;
	}

	
	/**
	 * Returns whether the subject has eaten in the last iteration
	 * @return
	 */
	
	
	/**
	 * Returns true if the subject has tried to eat, regardless of whether it could eat or not
	 * @return
	 */
	public boolean hasTriedToEat(){
		return triedToEat;
	}
	
	public void setTriedToEat(){
		triedToEat = true;
	}

	public void clearTriedToEAt(){
		triedToEat = false;
	}
	
	public RobotOld getRobot() {
		return robot;
	}

	public abstract List<Affordance> getPossibleAffordances();

	public abstract float getMinAngle();
	
	public abstract float getStepLenght();
	
	public abstract Affordance getHypotheticAction(Point3f pos, float theta, int intention);

	public abstract void deactivateHPCLayersRadial(LinkedList<Integer> indexList, float constant);
	
	public abstract void deactivateHPCLayersProportion(LinkedList<Integer> indexList, float proportion);

	public abstract void setExplorationVal(float i);

//	public abstract void restoreExploration();

	/**
	 * Returns the value of the position, orientation and intention
	 * @param point3f
	 * @param angle
	 * @return
	 */
	public abstract Map<Float, Float> getValue(Point3f point3f, int intention, float angleInterval, float distToWall);

	public abstract void remapLayers(LinkedList<Integer> indexList);

	public abstract float getValueEntropy();

	public abstract void reactivateHPCLayers(LinkedList<Integer> indexList);

	

	public Map<Point3f, Float> getValuePoints(){
		throw new NotImplementedException();
	}

	public abstract Affordance getForwardAffordance();
	
	public abstract Affordance getLeftAffordance();
	
	public abstract Affordance getRightAffordance();
	
	public Map<Integer, Float>  getPCActivity(){
		return new HashMap<>();
	}

}
