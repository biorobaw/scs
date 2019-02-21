package edu.usf.experiment.task;

import edu.usf.experiment.subject.Subject;
import edu.usf.experiment.universe.Universe;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyTask extends Task {

	private String text;

	public DummyTask(ElementWrapper params) {
		super(params);
		System.out.println("Creating a dummy task");
		text = params.getChildText("text");
	}

	public void perform(Universe u, Subject s){
		System.out.println("Performin dummy task");
		System.out.println(text);
	}

}
