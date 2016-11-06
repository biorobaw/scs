package edu.usf.experiment.plot;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.usf.experiment.utils.ElementWrapper;

public abstract class Plotter {
	
	private String logPath;
	private static ExecutorService pool = Executors.newFixedThreadPool(16);

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public Plotter(ElementWrapper params, String logPath){
		this.logPath = logPath;
	}

	public abstract Runnable plot();

	public static void plot(List<Plotter> plotters) {
		pool.submit(new Runnable() {
			public void run() {
				for (Plotter p : plotters)
					p.plot().run();
			}
		});
	}

}
