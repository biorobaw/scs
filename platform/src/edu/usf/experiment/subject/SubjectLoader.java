package edu.usf.experiment.subject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import edu.usf.experiment.robot.Robot;
import edu.usf.experiment.robot.RobotOld;
import edu.usf.experiment.utils.ElementWrapper;

/**
 * Loads a subject based on their non-fully qualified class name (simple name)
 * 
 * @author ludo
 * 
 */
public class SubjectLoader {

	private static SubjectLoader instance;
	private Map<String, Class<?>> classBySimpleName;

	public static SubjectLoader getInstance() {
		if (instance == null)
			instance = new SubjectLoader();
		return instance;
	}

	private SubjectLoader() {
//		Reflections reflections = new Reflections();
//		Set<Class<? extends Subject>> allClasses = reflections
//				.getSubTypesOf(Subject.class);
//		classBySimpleName = new HashMap<>();
//
//		for (Class<?> c : allClasses) {
//			classBySimpleName.put(c.getSimpleName(), c);
//		}
	}

	public Subject load(String subjectName, String groupName,
			ElementWrapper modelNode, Robot robot) {
		try {
			Constructor constructor;
			String name = modelNode.getChildText("name");
//			constructor = classBySimpleName.get(name).getConstructor(
//					String.class, String.class, ElementWrapper.class,
//					Robot.class);
			System.out.println(name);
			constructor = Class.forName(name).getConstructor(
					String.class, String.class, ElementWrapper.class,
					Robot.class);
			
			System.out.println("ROBOT: " +robot);
			
			Subject sub = (Subject)constructor.newInstance(subjectName,
					groupName, modelNode.getChild("params"), robot);
			return sub;
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
		
		System.out.println("ERROR : Subject not created");
		System.exit(0);
		
		return null;
	}

}
