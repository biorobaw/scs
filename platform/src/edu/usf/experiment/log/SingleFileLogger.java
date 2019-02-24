package edu.usf.experiment.log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.usf.experiment.Globals;
import edu.usf.experiment.utils.ElementWrapper;

public abstract class SingleFileLogger extends Logger {
	
	PrintWriter out;
	
	public SingleFileLogger(ElementWrapper params){
		super(params);
	}

	public void finalizeLog(){
		out.flush();
		out.close();
	}
	
	public abstract String getName();
	
	public void append(String text) {
		out.println(getName() + "\t" + text);
	}

	public abstract String getFileName();

	public String getLogPath() {
		return Globals.getInstance().get("logPath").toString();
	}
	
	@Override
	public void initLog() {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
		    fw = new FileWriter(getLogPath() + File.separator + getFileName(), true);
		    bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		} catch (IOException e) {
		}
	}


	
}