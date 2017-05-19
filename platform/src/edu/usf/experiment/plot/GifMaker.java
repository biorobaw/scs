package edu.usf.experiment.plot;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class GifMaker extends Plotter {

	private String plot;

	public GifMaker(ElementWrapper params, String logPath) {
		super(params, logPath);
		
		plot = params.getChildText("plot");
	}

	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		final String group = PropertyHolder.getInstance().getProperty("group");
		final String subject = PropertyHolder.getInstance().getProperty("subject");
		final String trial = PropertyHolder.getInstance().getProperty("trial");	
		
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/makegif.sh"), logPath + "makegif.sh");
				IOUtils.exec("bash makegif.sh " + group + " " + subject + " " + trial + " " + plot, logPath);
			}
		};
	}
	

}
