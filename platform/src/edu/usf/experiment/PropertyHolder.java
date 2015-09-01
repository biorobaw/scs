package edu.usf.experiment;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * This class mantains the properties assigned to an object.
 * 
 * @author ludo
 *
 */
public class PropertyHolder {

	private Dictionary<String, String> props;
	private static PropertyHolder instance = null;

	public static PropertyHolder getInstance(){
		if (instance == null)
			instance = new PropertyHolder();
		return instance;
	}
	
	private PropertyHolder() {
		props = new Hashtable<String, String>();
	}

	public String getProperty(String name) {
		return props.get(name);
	}

	public Object setProperty(String name, String property) {
		return props.put(name, property);
	}
}
