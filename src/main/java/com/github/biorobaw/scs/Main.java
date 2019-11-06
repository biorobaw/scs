package com.github.biorobaw.scs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import com.github.biorobaw.scs.experiment.Experiment;

public class Main {
	
	static String requiredConfigParams[] = new String[] {
			"experiment", // xml file specifying the experiment
			"config", 	  // config name
			"group", 	  // specifies an experimental group
			"run_id"	  // specifies an id for logging the subject(s)
	};
	
	
	/**
	 * Main function
	 * @param args command arguments
	 */
	public static void main(String[] args) {
		
		// process command arguments:
		var parsed_args = parseArguments(args);
		
		Experiment e = new Experiment(parsed_args);		
		e.run();
		System.exit(0);
	}
	
	/**
	 * Function that processess the console arguments
	 * @param args
	 * @return
	 */
	static public HashMap<String,Object> parseArguments(String args[]) {
		
		// Check input format:
		if(args.length != 3 && args.length != 4 ) {
			System.err.println("ERROR: expected format 'CONFIG_FILE CONFIG LOG_PATH [CREATE_LOGS]'");
			System.exit(-1);
		}
		
		System.out.println("[+] Parsing arguments...");
		// Get arguments:
		String configFile  = args[0]; // specifies file with the all configuration's arguments
		long configLine     = Long.parseLong(args[1]); // defines the line of the config file to load
		String baseLogPath = args[2]; // experiment log path
		
		// trim logpath
		if (baseLogPath.endsWith("/"))
			baseLogPath = baseLogPath.substring(0, baseLogPath.length()-1);
		
		// store parsed arguments
		HashMap<String,Object> parsed_args = new HashMap<>();
		parsed_args.put("baseLogPath", baseLogPath);
		parsed_args.put("configLine", configLine);
		parsed_args.put("configFile", configFile);
		parsed_args.put("create_logs", args.length == 4 && Boolean.parseBoolean(args[3]));
		
		// parse the configuration parameters from the configuration file:
		try (Stream<String> lines = Files.lines(Paths.get(configFile))) {

			// get lines iterator:
			var iterator = lines.iterator();
			
			// get the columns
			String columns = iterator.next();
			
			// skip to the line corresponding to the config
			for(int i=0; i<configLine; i++) iterator.next();
			
			// get config values:
			String values = iterator.next();
		    		    
		    // get tokens and trim them:
		    String[] valueTokens  = values.split("\\s+");	
		    String[] columnTokens = columns.split("\\s+");
		    
		    // Verify the number of parameters matches the number of columns
		    if(valueTokens.length != columnTokens.length) {
		    	System.err.println("ERROR: The number of values in the config does not match the number of columns");
		    	System.exit(-1);
		    }
		    
		    // tokenize and add to parsed arguments
		    for(int i=0;i<valueTokens.length;i++) {
		    	String key = removeQuotes(columnTokens[i].trim());
		    	String val = removeQuotes(valueTokens[i].trim());
		    	parsed_args.put(key, val);
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		//check required fields were included in the config file:
	    for(var field : requiredConfigParams) {
	    	if(parsed_args.get(field)==null) {
	    		System.err.println("column '+field+' missing from the config file");
		    	System.exit(-1);
	    	}
	    }
		
	    // set logPath of the configuration:
	    parsed_args.put("logPath", baseLogPath +"/" +parsed_args.get("config"));
	    
	    
	    return parsed_args;
		
	}
	
	/**
	 * Function to remove opening and closing quotation marks from strings
	 * @param arg
	 * @return
	 */
	private static String removeQuotes(String arg) {
		if(arg.charAt(0)=='"') arg = arg.substring(1, arg.length()-1);
		return arg;
	}

}
