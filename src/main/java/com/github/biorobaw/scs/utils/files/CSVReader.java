package com.github.biorobaw.scs.utils.files;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CSVReader {
	
	/**
	 * Load a csv file into a table of strings (does not remove blank lines)
	 * @param filename   
	 * @param separator 
	 * @param warning    debugging warning to be added if filename is null
	 * @return
	 */
	static public String[][] loadCSV(String filename, String separator,String warning){
		
		if (filename==null) {
			System.out.println("WARNING: " + warning + " filename not specified");
			return null;
		}
		String[][] result = null;
		try {
			// read full file and split by line:
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
	
	/**
	 * Load a csv file into a table of strings (does not remove blank lines nor end lines)
	 * @param filename   
	 * @param separator 
	 * @return
	 */
	static public String[][] loadCSV(String filename,String separator)
	{
		return loadCSV(filename,separator,"");
	}
	
	/**
	 * Load a csv file into a table of strings (does not remove blank lines). 
	 * By default, whitespace is used to separate entries. 
	 * @param filename   
	 * @return
	 */
	static public String[][] loadCSV(String filename){
		return loadCSV(filename," ","");
	}
	
}