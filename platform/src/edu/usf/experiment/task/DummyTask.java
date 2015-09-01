package edu.usf.experiment.task;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public class DummyTask extends Task {

	private String text;

	public DummyTask(ElementWrapper params) {
		super(params);
		System.out.println("Creating a dummy task");
		text = params.getChildText("text");
	}

	@Override
	public void perform(Experiment experiments) {
		System.out.println("Performing dummy task at experiment level");
		System.out.println(text);
	}

	@Override
	public void perform(Trial trial) {
		System.out.println("Performing dummy task at trial level");
		System.out.println(text);
	}

	@Override
	public void perform(Episode episode) {
		System.out.println("Performin dummy task at episode level");
		System.out.println(text);
	}

}
