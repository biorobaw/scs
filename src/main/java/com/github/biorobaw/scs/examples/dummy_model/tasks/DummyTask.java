package com.github.biorobaw.scs.examples.dummy_model.tasks;

import com.github.biorobaw.scs.tasks.cycle.CycleTask;
import com.github.biorobaw.scs.utils.files.XML;

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
