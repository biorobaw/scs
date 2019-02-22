package edu.usf.experiment.condition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.utils.ElementWrapper;

/**
 * Conditions signal whether a certain condition holds or not. They usually
 * query information about the subject and the universe.
 * 
 * @author ludo
 *
 */
public abstract class Condition {

	public abstract boolean holds();
	
	public void newEpisode() {}
	
	public static List<Condition> load(ElementWrapper conditionNodes) {
		List<Condition> res = new LinkedList<Condition>();
		List<ElementWrapper> conditionList = conditionNodes
				.getChildren("condition");
		for (ElementWrapper conditionNode : conditionList) {
			try {
				Constructor constructor = Class.forName(
						conditionNode.getChildText("name")).getConstructor(
						ElementWrapper.class);
				Condition plotter = (Condition) constructor
						.newInstance(conditionNode.getChild("params"));
				res.add(plotter);
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
		}
		return res;
	}
	

}
