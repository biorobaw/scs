package edu.usf.experiment.plot;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class CopyMazeFile extends Plotter {

	public CopyMazeFile(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public Runnable plot() {
//		PropertyHolder props = PropertyHolder.getInstance();
		final String logPath = getLogPath();
//		final String mazeFile = props.getProperty("maze.file");
		final String mazeFile = (String)Globals.getInstance().get("maze.file");
		return new Runnable(){
			@Override
			public void run() {
				IOUtils.copyFile(mazeFile,logPath + "maze.xml");				
			}
		};
		
	}

}
