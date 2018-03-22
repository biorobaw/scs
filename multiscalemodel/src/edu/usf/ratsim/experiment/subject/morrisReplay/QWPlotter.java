package edu.usf.ratsim.experiment.subject.morrisReplay;

import java.util.List;

import edu.usf.experiment.plot.Plotter;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class QWPlotter extends Plotter {
	
	String Wfile;
	List<String> Qfiles;
	String args = "";
	
	public QWPlotter(ElementWrapper params, String logPath){
		super(params,logPath);
		
		Wfile = params.getChildText("Wfile");
		Qfiles = params.getChildStringList("Qfiles");
		 
		args = " " + Wfile ;
		for(String s : Qfiles) args += " " + s ;
		
		
	}
	
	
	@Override
	public Runnable plot() {
		final String logPath = getLogPath();
		
		return new Runnable(){
			@Override
			public void run() {
					
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/Replay/plotQWMatrix.r"), logPath + "/plotQWMatrix.r");
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/Replay/plotQWUtilities.r"), logPath + "/plotQWUtilities.r");
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/Replay/plotUtilities.r"), logPath + "/plotUtilities.r");
				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/LoadBinary.r"), logPath + "/LoadBinary.r");
//				IOUtils.copyResource(getClass().getResource("/edu/usf/experiment/plot/Replay/replayLengthStatisticsAveraging.r"), logPath + "/replayLengthStatisticsAveraging.r");				
				IOUtils.exec("Rscript plotQWMatrix.r" + args , logPath);
				
			}
		};
	}

}

