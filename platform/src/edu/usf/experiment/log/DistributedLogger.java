package edu.usf.experiment.log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class DistributedLogger extends Logger {
	

	public DistributedLogger(ElementWrapper params){
		super(params);
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
	
	@Override
	public void initLog() {
		// TODO Auto-generated method stub
		
	}
	
	public String getLogPath() {
		return Globals.getInstance().get("logPath").toString();
	}

	public abstract String getHeader();
	
	public abstract String getFileName();
	
	

}
