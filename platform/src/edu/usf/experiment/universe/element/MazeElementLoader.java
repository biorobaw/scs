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
	
	public MazeElement load(ElementWrapper node){
		try {
			Constructor constructor;
//			constructor = classBySimpleName.get(
//					universeNode.getChildText("name")).getConstructor(
//					ElementWrapper.class);
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
