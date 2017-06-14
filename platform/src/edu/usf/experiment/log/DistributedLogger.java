package edu.usf.experiment.log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.usf.experiment.utils.ElementWrapper;

public abstract class DistributedLogger extends Logger {
	
	private String logPath;

	public DistributedLogger(ElementWrapper params, String logPath){
		super(params, logPath);
		
		this.setLogPath(logPath);
	}

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

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public abstract String getHeader();

}
