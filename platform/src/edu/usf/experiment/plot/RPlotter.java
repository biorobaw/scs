package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class RPlotter extends Plotter {

	private String plotScript;
	private String filename;

	public RPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		plotScript = params.getChildText("plotScript");
		String[] path = plotScript.split("/");
		filename = path[path.length-1];
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource(plotScript), logPath + filename);
		IOUtils.exec("Rscript " + filename, logPath);
	}

}
