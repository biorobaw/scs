package edu.usf.experiment.universe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Universe {

	/**
	 * Make a step in simulation/real world. The amount of time is up to the universe.
	 */
	public abstract void step();

	/**
	 * Set a reference to the robot. Used to give the robot feedback about its
	 * actions, when it is not able to deduce it by itself.
	 * 
	 * @param robot
	 */
	public abstract void setRobot(Robot robot);
	
	public abstract void clearState();
	
	public static Universe universe = null;
	
	static public Universe load(ElementWrapper root, String logPath) {
		ElementWrapper universeNode = root.getChild("universe");
		try {
			Constructor constructor;
//			constructor = classBySimpleName.get(
//					universeNode.getChildText("name")).getConstructor(
//					ElementWrapper.class);
			constructor = Class.forName(
					universeNode.getChildText("name")).getConstructor(
					ElementWrapper.class, String.class);
			universe = (Universe) constructor.newInstance(universeNode.getChild("params"), logPath);
			return universe;
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
	
	public static Universe getUniverse() {
		return universe;
	}

}
