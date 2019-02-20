package edu.usf.experiment.Deprecated.plot;

import java.net.URL;

import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class CSVConverter extends Plotter {

	public CSVConverter(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyFile("platform/src/edu/usf/experiment/plot/convert.r", logPath + "convert.r");
				IOUtils.exec("Rscript convert.r", logPath);
				IOUtils.delete(logPath + "convert.r");			
			}
		};
		
	}

}
