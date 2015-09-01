package edu.usf.experiment.log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.usf.experiment.Episode;
import edu.usf.experiment.Experiment;
import edu.usf.experiment.Trial;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class Logger {
	
	private String logPath;

	public Logger(ElementWrapper params, String logPath){
		this.setLogPath(logPath);
	}

	public abstract void log(Episode episode);
	
	public abstract void log(Trial trial);
	public abstract void log(Experiment experiment);

	public abstract void finalizeLog();
	
	public PrintWriter getWriter() {
		PrintWriter writer = null;
		
			try {
				// Writer with auto flush
//				String logDir = PropertyHolder.getInstance().getProperty("log.directory");
				writer = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(new File(getLogPath() + getFileName()))),
						true);
				writer.println(getHeader());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		return writer;
	}

	public abstract String getHeader();

	public abstract String getFileName();

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	

	

}
