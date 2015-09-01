package edu.usf.experiment.plot;

import java.net.URL;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class CSVConverter extends Plotter {

	public CSVConverter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		String logPath = getLogPath();
		URL resource = getClass().getResource("/edu/usf/experiment/plot/convert.r");
		IOUtils.copyResource(resource,logPath + "convert.r");
		IOUtils.exec("Rscript convert.r", logPath);
		IOUtils.delete(logPath + "convert.r");
	}

}
