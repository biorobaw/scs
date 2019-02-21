//package edu.usf.experiment.universe;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//
//import edu.usf.experiment.utils.ElementWrapper;
//
///**
// * Loads a universe based on their non-fully qualified class name (simple name)
// * 
// * @author ludo
// * 
// */
//public class UniverseLoader {
//
//	private static UniverseLoader instance;
//	private static Universe universe = null;
//
//	public static UniverseLoader getInstance() {
//		if (instance == null)
//			instance = new UniverseLoader();
//		return instance;
//	}
//
//	private UniverseLoader() {
////		Reflections reflections = new Reflections();
////		Set<Class<? extends Universe>> allClasses = reflections
////				.getSubTypesOf(Universe.class);
////		classBySimpleName = new HashMap<>();
////
////		for (Class<?> c : allClasses) {
////			classBySimpleName.put(c.getSimpleName(), c);
////		}
//	}
//
//	
//	
//	public static Universe getUniverse(){
//		return universe;
//	}
//
//}
