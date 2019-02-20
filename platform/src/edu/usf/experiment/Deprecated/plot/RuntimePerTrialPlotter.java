package edu.usf.experiment.Deprecated.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class RuntimePerTrialPlotter extends Plotter {

	public RuntimePerTrialPlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotRuntimesPerTrial.r"), logPath + "/plotRuntimesPerTrial.r");
				IOUtils.exec("Rscript plotRuntimesPerTrial.r", logPath);
			}
		};
	}

}
