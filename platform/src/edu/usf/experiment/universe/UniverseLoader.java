package edu.usf.experiment.universe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;

/**
 * Loads a universe based on their non-fully qualified class name (simple name)
 * 
 * @author ludo
 * 
 */
public class UniverseLoader {

	private static UniverseLoader instance;

	public static UniverseLoader getInstance() {
		if (instance == null)
			instance = new UniverseLoader();
		return instance;
	}

	private UniverseLoader() {
//		Reflections reflections = new Reflections();
//		Set<Class<? extends Universe>> allClasses = reflections
//				.getSubTypesOf(Universe.class);
//		classBySimpleName = new HashMap<>();
//
//		for (Class<?> c : allClasses) {
//			classBySimpleName.put(c.getSimpleName(), c);
//		}
	}

	public Universe load(ElementWrapper root, String logPath) {
		ElementWrapper universeNode = root.getChild("universe");
		try {
			Constructor constructor;
			//System.out.println("universe.load()");
//			constructor = classBySimpleName.get(
//					universeNode.getChildText("name")).getConstructor(
//					ElementWrapper.class);
			constructor = Class.forName(
					universeNode.getChildText("name")).getConstructor(
					ElementWrapper.class, String.class);
			System.out.println("Constructor: " + constructor.toString());
			Universe universe = (Universe) constructor.newInstance(universeNode
					.getChild("params"), logPath);
			System.out.println("Universe loaded: "+ universe);
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

}
