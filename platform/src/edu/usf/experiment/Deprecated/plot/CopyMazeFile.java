package edu.usf.experiment.Deprecated.plot;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class CopyMazeFile extends Plotter {

	public CopyMazeFile(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
		final String logPath = Globals.getInstance().get("logPath").toString();
		final String mazeFile = Globals.getInstance().get("maze.file").toString();
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyFile(mazeFile,logPath + "maze.xml");				
			}
		};
		
	}

}
