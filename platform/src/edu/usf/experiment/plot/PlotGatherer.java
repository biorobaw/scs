package edu.usf.experiment.plot;

import java.io.File;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class PlotGatherer extends Plotter {

	public PlotGatherer(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		System.out.println("Gathering plots on " + logPath);
		new File(logPath + "/plots/").mkdir();
		IOUtils.exec("find . -iname *.pdf -exec ln -f {} plots/ ;", logPath);
		IOUtils.exec("find . -iname *.png -exec ln -f {} plots/ ;", logPath);
	}

}
