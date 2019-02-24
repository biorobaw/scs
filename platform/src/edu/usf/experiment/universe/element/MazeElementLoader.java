package edu.usf.experiment.universe.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.usf.experiment.utils.ElementWrapper;

public class MazeElementLoader {
	
	private static MazeElementLoader instance;
	
	public static MazeElementLoader getInstance(){
		if (instance ==null) instance = new MazeElementLoader();
		return instance;
	}

	private MazeElementLoader() {
		// TODO Auto-generated constructor stub
	}
	
	

}
