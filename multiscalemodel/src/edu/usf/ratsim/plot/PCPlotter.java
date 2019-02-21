package edu.usf.ratsim.plot;

import edu.usf.experiment.deprecated.plot.Plotter;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class PCPlotter extends Plotter {

	public PCPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		return new Runnable() {
			public void run() {
				IOUtils.copyResource(getClass().getResource("/edu/usf/ratsim/experiment/plot/plotPlaceCells.r"), logPath + "plotPlaceCells.r");
				IOUtils.exec("Rscript plotPlaceCells.r", logPath);
			}
		};
		
	}

}
