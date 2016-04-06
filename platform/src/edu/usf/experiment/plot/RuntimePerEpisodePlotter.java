package edu.usf.experiment.plot;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class RuntimePerEpisodePlotter extends Plotter {

	public RuntimePerEpisodePlotter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/plotRuntimesPerEpisode.r"), logPath + "/plotRuntimesPerEpisode.r");
		
		IOUtils.exec("Rscript plotRuntimesPerEpisode.r", logPath);
	}

}
