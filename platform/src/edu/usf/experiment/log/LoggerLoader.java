package edu.usf.experiment.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.usf.experiment.utils.ElementWrapper;

/**
 * Loads conditions based on their non-fully qualified class name (simple name)
 * 
 * @author ludo
 * 
 */
public class LoggerLoader {

	private static LoggerLoader instance;
	private Map<String, Class<?>> classBySimpleName;

	public static LoggerLoader getInstance() {
		if (instance == null)
			instance = new LoggerLoader();
		return instance;
	}

	private LoggerLoader() {
		// Reflections reflections = new Reflections();
		// Set<Class<? extends Logger>> allClasses = reflections
		// .getSubTypesOf(Logger.class);
		// classBySimpleName = new HashMap<>();
		//
		// for (Class<?> c : allClasses) {
		// classBySimpleName.put(c.getSimpleName(), c);
		// }
	}

	public List<Logger> load(ElementWrapper loggerNodes, String logPath) {
		List<Logger> res = new LinkedList<Logger>();
		if (loggerNodes != null) {
			List<ElementWrapper> loggerList = loggerNodes.getChildren("logger");
			for (ElementWrapper loggerNode : loggerList) {
				try {
					Constructor constructor;
					// constructor = classBySimpleName.get(
					// conditionNode.getChildText("name")).getConstructor(
					// ElementWrapper.class);
					constructor = Class
							.forName(loggerNode.getChildText("name"))
							.getConstructor(ElementWrapper.class, String.class);
					Logger plotter = (Logger) constructor
							.newInstance(loggerNode.getChild("params"), logPath);
					res.add(plotter);
				} catch (SecurityException e) {
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
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}

}
