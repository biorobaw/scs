package edu.usf.experiment.deprecated.plot;

import edu.usf.experiment.Globals;
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
		Globals g = Globals.getInstance();
		String trialName = g.get("trial").toString();
		String groupName = g.get("group").toString();
		String subName = g.get("subName").toString();
		
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/makegif.sh"), logPath + "makegif.sh");
				IOUtils.exec("bash makegif.sh " + groupName + " " + subName + " " + trialName + " " + plot, logPath);
			}
		};
	}
	

}
