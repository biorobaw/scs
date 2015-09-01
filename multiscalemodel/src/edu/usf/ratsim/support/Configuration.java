package edu.usf.ratsim.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class Configuration {
	public final static String PROP_FILE = "/resources/config.properties";
	private static Properties configuracion = new Properties();
	private static Configuration singleton = new Configuration();

	private Configuration() {
		InputStream in;
		in = getClass().getResourceAsStream(PROP_FILE);
		try {
			configuracion.load(in);
			in.close();
		} catch (IOException e) {
			System.err
					.println("Configuration::Error al cargar el archivo de configuracion.");
		}
	}

	public static String getString(String propertyName) {
		return configuracion.getProperty(propertyName);
	}

	public static int getInt(String propertyName) {
		return Integer.parseInt(configuracion.getProperty(propertyName));
	}

	public static double getDouble(String propertyName) {
		return Double.parseDouble(configuracion.getProperty(propertyName));
	}

	public static float getFloat(String propertyName) {
		return Float.parseFloat(configuracion.getProperty(propertyName));
	}

	public static boolean getBoolean(String propertyName) {
		return Boolean.parseBoolean(configuracion.getProperty(propertyName));
	}

	public static Object getObject(String objectName) {
		String objectClassName = Configuration.getString(objectName);
		// Reflexion para levantar la clase desde archivo de configuracion
		Class<?>[] types = new Class[] {};
		@SuppressWarnings("rawtypes")
		Constructor cons = null;
		Object result = null;

		try {
			cons = Class.forName(objectClassName).getConstructor(types);
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
		Object[] args = new Object[] {}; // constructor sin argumentos
		try {
			result = cons.newInstance(args);
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

	public static void setProperty(String name, String val) {
		configuracion.setProperty(name, val);
	}

}
