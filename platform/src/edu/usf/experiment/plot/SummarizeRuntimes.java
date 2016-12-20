package edu.usf.experiment.plot;

import java.net.URL;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class SummarizeRuntimes extends Plotter {

	public SummarizeRuntimes(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		String logPath = getLogPath();
		URL resource = getClass().getResource("/edu/usf/experiment/plot/summarize.r");

		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(resource,logPath + "summarize.r");
				IOUtils.exec("Rscript summarize.r", logPath);
			}
		};
	}

}
