package com.github.biorobaw.scs.experiment.task.cycle;

import com.github.biorobaw.scs.utils.XML;

public class DummyTask extends CycleTask {

	private String text;

	public DummyTask(XML xml) {
		super(xml);
		System.out.println("Creating a dummy task");
		text = xml.getAttribute("text");
	}

	public long perform(){
		System.out.println("Performing dummy task");
		System.out.println(text);
		return 0;
	}

}
