package edu.usf.experiment;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Globals {
	
	/**
	 * List of globals from platform:
	 * 
	 * load all globals defined in console command
	 * 
	 *Boolean done				Signals that the experiment is done - used with done condition
	 *
	 *
	 *String group				Group to which the subject belongs to
	 *String subName			Subject name
	 *String trial				trial being executed
	 *Integer episode			episode being executed
	 *Integer cycle				cycle (iteration) being executed
	 *
	 *String maze.file			Address of maze file
	 *String baseLogPath		The base log folder for all configs
	 *String configFile			The csv file specifying the parameters of each execution
	 *String configId			The line of the csv to be executed
	 *String logPath			Log folder for specified config
	 *String episodeLogPath 	Indicates the path to store the current episode information
	 *	
	 *
	 *Display and simulation control:
	 *Boolean 	display
	 *Boolean 	syncDisplay
	 *Long 		seed
	 *Boolean 	collisionDetection
	 *Integer	runLevel 		The level of execution 0=PreExperiment 1=Experiment 2=PostExperiment 3=AllPhases
	 *
	 *
	 *
	 *The next 3 are used for loading a particular episode - define in xml or command line
	 *Integer loadEpisode
	 *String  loadTrial
	 *String  loadPath	
	 *String  loadSnapshot         = loadEpisode + "snapshots/" +"g"+group+"-s"+subName+"-t"+trial + "-e" + episode  // snapshot load path
	 *
	 *
	 *String  savePath         snapshot savepath = logPath + "/snapshots/" +saveFile + "/"
	 *String  saveSnapshotPath = savePath + "g" + grou + "-s" + subName + "-t"+trial + "-e" + episode + "-"  //string indicating where to save snapshots, append file names to this variable 
	 */
	
	
	
	public HashMap<String,Object> global;
	
	private static Globals instance = null;
	protected Globals(){
		global = new LinkedHashMap<String,Object>();
		global.put("done", false);
		global.put("pause", false);
	}
	
	public static Globals getInstance(){
		if(instance==null) {
			instance = new Globals();
		}
		return instance;
	}
	
	synchronized public void put(String id,Object globalVar){
		global.put(id,globalVar);
	}
	
	synchronized public Object get(String id){
		return global.get(id);
	}
	
	synchronized public void remove(String id){
		global.remove(id);
	}
}
