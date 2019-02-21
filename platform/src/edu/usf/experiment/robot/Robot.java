package edu.usf.experiment.robot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * @author gtejera, mllofriu
 * 
 */
public abstract class Robot {
	
	/**
	 * Method invocked at the beginning of each session
	 */
	public abstract void startRobot();
	
	/**
	 * Returns the radius of the cylinder that wraps the robot
	 * @return the radius of the cyclinder that wraps the robot
	 */
	public abstract float getRadius();

	public abstract void clearState();

	static public Robot load(ElementWrapper root, Universe universe) {
		ElementWrapper robotNode = root.getChild("robot");
		try {
			Constructor constructor;

			constructor = Class.forName(
					robotNode.getChildText("name")).getConstructor(
							ElementWrapper.class, Universe.class);
			
			Robot robot = (Robot) constructor.newInstance(robotNode
					.getChild("params"), universe);
			
			return robot;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
