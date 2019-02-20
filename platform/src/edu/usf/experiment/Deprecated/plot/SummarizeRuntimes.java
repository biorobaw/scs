package edu.usf.experiment.Deprecated.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class SummarizeRuntimes extends Plotter {

	public SummarizeRuntimes(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyFile("platform/src/edu/usf/experiment/plot/summarize.r",logPath + "summarize.r");
				IOUtils.exec("Rscript summarize.r", logPath);
			}
		};
	}

}
