package edu.usf.experiment.condition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;

/**
 * Loads conditions based on their non-fully qualified class name (simple name)
 * @author ludo
 *
 */
public class ConditionLoader {

	private static ConditionLoader instance;
	private Map<String, Class<?>> classBySimpleName;

	public static ConditionLoader getInstance() {
		if (instance == null)
			instance = new ConditionLoader();
		return instance;
	}

	private ConditionLoader() {
//		Reflections reflections = new Reflections();
//		Set<Class<? extends Condition>> allClasses = reflections
//				.getSubTypesOf(Condition.class);
//		classBySimpleName = new HashMap<>();
//
//		for (Class<?> c : allClasses) {
//			classBySimpleName.put(c.getSimpleName(), c);
//		}
	}

	public List<Condition> load(ElementWrapper conditionNodes) {
		List<Condition> res = new LinkedList<Condition>();
		List<ElementWrapper> conditionList = conditionNodes
				.getChildren("condition");
		for (ElementWrapper conditionNode : conditionList) {
			try {
				Constructor constructor;
//				constructor = classBySimpleName.get(
//						conditionNode.getChildText("name")).getConstructor(
//						ElementWrapper.class);
				constructor = Class.forName(
						conditionNode.getChildText("name")).getConstructor(
						ElementWrapper.class);
				Condition plotter = (Condition) constructor
						.newInstance(conditionNode.getChild("params"));
				res.add(plotter);
			} catch (NoSuchMethodException | SecurityException e) {
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
