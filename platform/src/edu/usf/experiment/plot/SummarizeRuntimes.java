package edu.usf.experiment.plot;

import java.net.URL;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class SummarizeRuntimes extends Plotter {

	public SummarizeRuntimes(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		URL resource = getClass().getResource("/edu/usf/experiment/plot/summarize.r");
		IOUtils.copyResource(resource,logPath + "summarize.r");
		IOUtils.exec("Rscript summarize.r", logPath);
//		IOUtils.delete(logPath + "summarize.r");
	}

}
