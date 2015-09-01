package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class ValuePlotter extends Plotter {

	public ValuePlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotValue.r"), logPath + "plotValue.r");
		IOUtils.exec("Rscript plotValue.r", logPath);
	}

}
