package edu.usf.ratsim.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.usf.micronsl.module.Module;

public class ReflexionLoader {

	public static Module getReflexionModel(String module, String moduleName,
			Object owner) {
		Module result = null;

		Class<?>[] types = new Class[] { String.class, Module.class };
		@SuppressWarnings("rawtypes")
		Constructor cons = null;
		try {
			cons = Class.forName(module).getConstructor(types);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[] args = new Object[] { moduleName, owner };
		try {
			result = (Module) cons.newInstance(args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
