package edu.usf.experiment.utils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CSVReader {
	static public String[][] loadCSV(String filename, String separator,String warning){
		
		if (filename==null) {
			System.out.println("WARNING: " + warning + " filename nor specified");
			return null;
		}
		String[][] result = null;
		try {
			Scanner scanner = new Scanner(new File(filename));
			String entireFileText = scanner.useDelimiter("\\A").next();
			scanner.close();
			String[] lines = entireFileText.split("\n");
			
			result = new String[lines.length][];
			// read line by line
			for(int i=0;i<lines.length;i++)
				result[i] = lines[i].split(separator);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
		
	}
	
	static public String[][] loadCSV(String filename,String separator)
	{
		return loadCSV(filename,separator,"");
	}
	
	static public String[][] loadCSV(String filename){
		return loadCSV(filename," ","");
	}
	
}
