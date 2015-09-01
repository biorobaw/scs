package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class PathPlotter extends Plotter {

	public PathPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotPath.r"), logPath + "plotPath.r");
		IOUtils.exec("Rscript plotPath.r", logPath);
	}

}
