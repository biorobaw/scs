package edu.usf.experiment.plot;

import edu.usf.experiment.PropertyHolder;
import edu.usf.experiment.utils.ElementWrapper;
import edu.usf.experiment.utils.IOUtils;

public class CopyMazeFile extends Plotter {

	public CopyMazeFile(ElementWrapper params, String logPath) {
		super(params, logPath);
	}

	@Override
	public void plot() {
		PropertyHolder props = PropertyHolder.getInstance();
		String logPath = getLogPath();
		String mazeFile = props.getProperty("maze.file");
		IOUtils.copyFile(mazeFile,logPath + "maze.xml");
	}

}
