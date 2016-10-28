package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class ValuePlotterWithIntention extends Plotter {

	public ValuePlotterWithIntention(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotValueIntention.r"), logPath + "plotValueIntention.r");
		IOUtils.exec("Rscript plotValueIntention.r", logPath);
	}

}
