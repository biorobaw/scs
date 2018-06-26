package edu.usf.experiment;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Globals {
	
	/**
	 * List of globals from platform:
	 * 
	 * load all globals defined in console command
	 * 
	 *Boolean pause				Indicates to pause simulation
	 *Boolean done				Signals that the experiment is done - used with done condition
	 *Integer simulationSpeed	Chooses simulation speed from a predetermined value
	 *
	 *
	 *String group				Group to which the subject belongs to
	 *String subName			Subject name
	 *String trial				trial being executed
	 *Integer episode			episode being executed
	 *Integer cycle				cycle (iteration) being executed
	 *
	 *String maze.file			Address of maze file
	 *String logPath			Log folder path
	 *String episodeLogPath 	Indicates the path to store the current episode information
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
	
	public void put(String id,Object globalVar){
		global.put(id,globalVar);
	}
	
	public Object get(String id){
		return global.get(id);
	}
	
	public void remove(String id){
		global.remove(id);
	}
}
