package edu.usf.experiment.universe.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import edu.usf.experiment.universe.wall.Wall;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class MazeElement {
	public LinkedList<Wall> walls = new LinkedList<Wall>();

	public MazeElement(ElementWrapper params){
		
	}
	
	static public MazeElement load(ElementWrapper node){
		try {
			Constructor constructor;
			String classname = "edu.usf.experiment.universe.element."+node.getChildText("name");
			constructor = Class.forName(classname).getConstructor(
					ElementWrapper.class);
			MazeElement element = (MazeElement) constructor.newInstance(node
					.getChild("params"));
			return element;
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
