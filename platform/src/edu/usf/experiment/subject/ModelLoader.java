package edu.usf.experiment.subject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.micronsl.Model;

/**
 * Loads a subject based on their non-fully qualified class name (simple name)
 * 
 * @author ludo
 * 
 */
public class ModelLoader {

	private static ModelLoader instance;
	private Map<String, Class<?>> classBySimpleName;

	public static ModelLoader getInstance() {
		if (instance == null)
			instance = new ModelLoader();
		return instance;
	}

	private ModelLoader() {

	}

	public Model load(ElementWrapper modelNode, Robot robot) {
		try {
			Constructor constructor;
			String name = modelNode.getChildText("name");
//			constructor = classBySimpleName.get(name).getConstructor(
//					String.class, String.class, ElementWrapper.class,
//					Robot.class);
			constructor = Class.forName(name).getConstructor(
					ElementWrapper.class,
					Robot.class);
			Model model = (Model) constructor.newInstance(modelNode.getChild("params"), robot);
			return model;
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
		System.out.println("Model did not load correctly");
		System.exit(-1);
		return null;
	}

}
