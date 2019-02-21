package edu.usf.experiment.task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Task {

	public Task(ElementWrapper params) {

	}

	
	public abstract void perform(Universe u, Subject s);
	
	
	public void newEpisode() {
		
	};
	
	public void endEpisode() {
		
	}
	
	public void newTrial() {
		
	}
	
	public void endTrial() {
		
	}
	
	public void newExperiment() {
		
	}
	
	public void endExperiment() {
		
	}
	
	public static List<Task> loadTask(ElementWrapper taskNodes) {
		List<Task> res = new LinkedList<Task>();
		if (taskNodes != null) {
			List<ElementWrapper> taskList = taskNodes.getChildren("task");
			for (ElementWrapper taskNode : taskList) {
				try {
					Constructor constructor;
					// constructor = classBySimpleName.get(
					// taskNode.getChildText("name")).getConstructor(
					// ElementWrapper.class);
					constructor = Class.forName(taskNode.getChildText("name"))
							.getConstructor(ElementWrapper.class);
					Task task = (Task) constructor.newInstance(taskNode
							.getChild("params"));
					res.add(task);
				} catch (NoSuchMethodException  e) {
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
					System.exit(-1);
				}
			}
		}
		return res;
	}
	

}
