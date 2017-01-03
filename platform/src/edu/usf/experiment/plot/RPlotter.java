package edu.usf.experiment.plot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class RPlotter extends Plotter {

	private String plotScript;
	private String filename;
	private static ExecutorService pool = Executors.newFixedThreadPool(16);

	public RPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		plotScript = params.getChildText("plotScript");
		String[] path = plotScript.split("/");
		filename = path[path.length-1];
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(getClass().getResource(plotScript), logPath + filename);
				IOUtils.exec("Rscript " + filename, logPath);
			}
		};
	}

}
