package edu.usf.experiment.plot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.usf.experiment.utils.ElementWrapper;

public abstract class Plotter {

	private String logPath;
	// private static ExecutorService pool = Executors.newFixedThreadPool(100);
	private static Set<Thread> threads = new HashSet<Thread>();

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public Plotter(ElementWrapper params, String logPath) {
		this.logPath = logPath;
	}

	public abstract Runnable plot();

	public static synchronized void plot(final List<Plotter> plotters) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (Plotter p : plotters)
					p.plot().run();
			}
		});
		threads.add(t);
		t.start();
	}

	public static void join() {
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		threads.clear();
	}

}
