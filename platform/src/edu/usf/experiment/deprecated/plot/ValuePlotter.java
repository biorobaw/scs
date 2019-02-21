package edu.usf.experiment.deprecated.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class ValuePlotter extends Plotter {

	public ValuePlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotValue.r"), logPath + "plotValue.r");
				IOUtils.exec("Rscript plotValue.r", logPath);
			}
		};
	}

}
