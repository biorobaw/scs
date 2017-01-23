package edu.usf.experiment.robot;

import java.util.List;

import javax.vecmath.Point3f;

import edu.usf.experiment.subject.SubjectOld;
import edu.usf.experiment.subject.affordance.Affordance;
import edu.usf.experiment.universe.Feeder;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * This class represents the body of the subject. It allows it to make movements and query information.
 */

/**
 * @author gtejera, mllofriu
 * 
 */
public abstract class RobotOld extends Robot{
	
	public RobotOld(ElementWrapper params){
		
	}
	
	public RobotOld(ElementWrapper params, Universe u){
		
	}
	
	/**
	 * Makes the robot eat food
	 */
	public abstract void eat();

	/**
	 * Return whether the robot has found food in the environment
	 * 
	 * @return
	 */
	public abstract boolean hasFoundFood();

	/**
	 * Method invocked at the beginning of each session
	 */
	public abstract void startRobot();

	/**
	 * Move forward one step
	 */
	public abstract void forward(float distance);
	
	/**
	 * Makes the robot perform an action.
	 * 
	 * @param degrees
	 *            If degrees == 0, the robot goes forward. Else, it turns the
	 *            amount number of degrees. Negative degrees represent left
	 *            turns.
	 */
	public abstract void rotate(float degrees);

	public abstract Feeder getFlashingFeeder();

	public abstract boolean seesFlashingFeeder();

	public Feeder getClosestFeeder(){
		List<Feeder> feeders = getVisibleFeeders(null);
		float minDist = Float.MAX_VALUE;
		Feeder closest = null;
		for (Feeder f : feeders){
			float dist = f.getPosition().distance(new Point3f());
			if (dist < minDist){
				closest = f;
				minDist = dist;
			}
		}
		return closest;	
	}

	public abstract boolean isFeederClose();

	/**
	 * Checks each passed affordance to decide if it is realizable or not
	 * @param possibleAffordances
	 * @return
	 */
	public abstract List<Affordance> checkAffordances(List<Affordance> possibleAffordances);
	
	public abstract boolean checkAffordance(Affordance af);

	public abstract void executeAffordance(Affordance selectedAction, SubjectOld sub);
	
	/**
	 * Returns the feeders visible to the robot
	 * @param i Feeder to ignore
	 * @return
	 */
	public abstract List<Feeder> getVisibleFeeders(int[] is);
	
	public abstract List<Point3f> getVisibleWallEnds();

	public List<Point3f> getInterestingPoints() {
		List<Point3f> points = getVisibleWallEnds();
		int[] exclude = {-1, -1};
		List<Feeder> feeders = getVisibleFeeders(exclude);
		for (Feeder f : feeders)
			points.add(f.getPosition());
		return points;
	}

	public abstract int getLastAteFeeder();

	public abstract int getLastTriedToEatFeeder() ;
	public abstract boolean hasFoundPlatform();

	/**
	 * Move the robot in a continous fashion, as opposed to step motions
	 * @param lVel Linear velocity, positive values produce forward motions
	 * @param angVel Angular velocity, positive values mean left rotations
	 */
	public abstract void moveContinous(float lVel, float angVel);
	
	public void processAction(RobotAction action){};
	
	public void executeTimeStep(float deltaT){};
}
