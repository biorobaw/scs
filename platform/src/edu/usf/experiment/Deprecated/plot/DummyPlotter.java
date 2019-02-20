package edu.usf.experiment.Deprecated.plot;

import edu.usf.experiment.utils.ElementWrapper;

public class DummyPlotter extends Plotter {

	public DummyPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
		System.out.println("Creating a dummy plotter");
	}

	@Override
	public Runnable plot() {
		return new Runnable(){
			@Override
			public void run() {
				System.out.println("Plotting...");
			}
		};
	}

}
