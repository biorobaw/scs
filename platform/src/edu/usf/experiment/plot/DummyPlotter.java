package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;

public class DummyPlotter extends Plotter {

	public DummyPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
		System.out.println("Creating a dummy plotter");
	}

	@Override
	public void plot() {
		System.out.println("Plotting...");
	}

}
