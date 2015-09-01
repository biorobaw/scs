package edu.usf.ratsim.experiment.plot;

import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class PCPlotter extends Plotter {

	public PCPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource("/edu/usf/ratsim/experiment/plot/plotPlaceCells.r"), logPath + "plotPlaceCells.r");
		IOUtils.exec("Rscript plotPlaceCells.r", logPath);
	}

}
