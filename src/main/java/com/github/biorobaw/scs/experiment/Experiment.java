package com.github.biorobaw.scs.experiment;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.github.biorobaw.scs.gui.Display;
import com.github.biorobaw.scs.maze.Maze;
import com.github.biorobaw.scs.model.Model;
import com.github.biorobaw.scs.simulation.AbstractSimulator;
import com.github.biorobaw.scs.simulation.scripts.Script;
import com.github.biorobaw.scs.utils.XML;
import com.github.biorobaw.scs.utils.IOUtils;
import com.github.biorobaw.scs.utils.RandomSingleton;

/**
 * Defines an experiment to be performed as specified in the user experiment xml file.
 * See helloworld.xml for details.
 * 
 * When executed, this class executes a single run of the experiment for a given group.
 * 
 * @author gtejera,mllofriu,bucef
 * 
 */
public class Experiment implements Runnable {
	
	
	public static Experiment instance;
	
	private HashMap<String,Object> globals = new HashMap<>();
	public  Display display;
	public  AbstractSimulator simulator;
	public  Maze maze ;
	public  HashMap<String, Model> subjects = new HashMap<>();
	public  ExperimentController controller = new ExperimentController(this);
	
	protected List<Script> tasks   = new LinkedList<>();
	protected List<Trial> trials = new LinkedList<>();
	
	/** 
	 * Loads experiment from experiment file
	 * @param args arguments passed from command console and configuration file
	 */
	public Experiment(HashMap<String, Object> args) {
		
		// save singleton instance
		instance = this;
		
		// load globals and get root of xml file
		var root = loadGlobals(args);
		
		// set random seed (loads from globals if defined)
		setSeed();

		//  if necessary create and init logging dirs
		if((boolean)args.get("create_logs")) createAndInitLogFolder();
		
		
		// Load simulator
		System.out.println("Loading simulator...");
		simulator = root.getChild("simulator").loadObject();
		
		// Load the display. Must load after the simulator.
		System.out.println("Loading display...");
		display = root.getChild("display").loadObject();
		
		
		// Load maze and add elements to simulator
		System.out.println("[+] Loading maze");
		maze = root.getChild("maze").loadObject();
		// elements are added here and not in the constructor 
		// in case other mazes are created but don't want to be added.
		maze.addElementsToSimulator();
		
		// Load group subjects, the function returns the trials that need to be loaded
		// Also, each subject loads its robot and adds it to the simulation
		var trial_names = loadGroupSubjects(root);
		
		// Load experiment tasks
		System.out.println("[+] Loading Experiment Tasks");
		if(root.hasChild("experimentTasks"))
			tasks = root.getChild("experimentTasks")
					    .loadObjectList();
	

		// Load trials that apply to the subject
		var t_xmls = root.getChildrenMap("trial");
		for(var t : trial_names) {
			trials.add(t_xmls.get(t).loadObject(Trial.class));
		}
		

		//Load a saved state (if loading)
		load_saved_state();
		
	}

	public Model getSubject(String id) {
		return subjects.get(id);
	}
	
	public XML loadGlobals(HashMap<String, Object> args ) {
	    
		System.out.println("[+] Loading globals...");
		
		// load experiment file
	    var experiment = args.get("experiment").toString();
		XML root = new XML(experiment);
		
		// get variables defined in xml file 
		var variables = root.getVariables();
		globals.putAll(variables);
	    
		// add all arguments to global variables possibly overwriting xml variables
		for(var e : args.entrySet()) {
			setGlobal(e.getKey(), e.getValue());
		}
		
		//define other globals
		var maze = root.getChild("maze");
		var m ="";
		if(maze.hasAttribute("file"))
			m = args.get("baseLogPath") +"/mazes/" + maze.getAttribute("file");
		setGlobal("maze", m );
		setGlobal("trial","");
	    setGlobal("episode",-1);		
		
	    // add the globals to the variables in the xml file
	    for(var e : globals.entrySet()) variables.put(e.getKey(), e.getValue().toString());
	    
	    // Print important globals:
 		System.out.println("[+] Starting group  " + getGlobal("group"));
 		System.out.println("[+] Run id          " + getGlobal("run_id"));
 		System.out.println("[+] Log path        " + getGlobal("logPath"));
	    
		return root;

	}

	
	void createAndInitLogFolder() {
		String baseLogPath = getGlobal("baseLogPath");
		String logPath = getGlobal("logPath");
		
		//create log path directories
		System.out.println("[+] Creating directories");
		
		File file = new File(logPath +"/");
		file.mkdirs();
		
		File experimentFolder = new File(baseLogPath + "/experiments/");
		experimentFolder.mkdirs();
		
		File mazeFolder = new File(baseLogPath + "/mazes/");
		mazeFolder.mkdirs();
		
		
		//Copy experiment file
		String experimentFile = getGlobal("experiment");
		String experimentName = (new File(experimentFile)).getName();
		IOUtils.copyFile(experimentFile, baseLogPath + "/experiments/" + experimentName);	
		
		//Copy configuration file
		String configFile = getGlobal("configFile");
		IOUtils.copyFile(configFile, baseLogPath + "/configs.txt");	
		
		//Copy maze file
		XML root = new XML(experimentFile);
		String mazeFile = root.getChild("maze").getAttribute("file");
		if(mazeFile!=null) {
			String mazeName = (new File(mazeFile)).getName();
			IOUtils.copyFile(mazeFile, baseLogPath + "/mazes/" + mazeName);			
		}
		
		
	}

	void setSeed() {
		
		String seed_str = getGlobal("seed");
		Long seed =  seed_str!=null ? Long.parseLong(seed_str) : new Random().nextLong();
		
		if (seed_str != null) System.out.println("[+] Using seed from xml file");
		RandomSingleton.getInstance().setSeed(seed);
		System.out.println("[+] Using seed " + seed);
	}
	
	List<String> loadGroupSubjects(XML root) {
		// get group
		var group = root.getChild("groups").getChild(getGlobal("group"));
		
		// load all subjects in the group
		for(var subject : group.getChildren()) {
			String name = subject.getName(); //get subject id			
			subjects.put(name, subject.loadObject());
		}
		return group.getStringListAttr("trials");
		
	}


	void load_saved_state() {
		// TODO: Has to be reimplemented
		
//		if(g.get("loadPath")==null) return;
//				
//		String t = g.get("loadTrial").toString();
//		Integer e = Integer.parseInt(g.get("loadEpisode").toString());
//
//		//Pop trials until we get the trial we want
//		for(int i =0;;i++){
//			System.out.println(trials.get(0).getName() + " "  + t);
//			if(trials.get(0).getName().equals(t)) break;
//			else trials.remove(0);
//		}
//		
//		//pop episodes from the trial until we reach the episode being loaded
//		trials.get(0).startingEpisode = e;
////		g.put("episode",e);
//		
//		subject.getModel().load();
		
	}

	
	/***
	 * Runs the experiment for the especified subject. Just goes over trials and
	 * runs them all. It also executes tasks and plotters.
	 */
	public void run() {					
		controller.runExperiment();
	}
	
	
	public Maze getMaze() {
		return maze;
	}
	
	/**
	 * Function to get a global variable
	 * @param key the variable name
	 * @return	Returns the variable or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getGlobal(String key) {
		return (T)globals.get(key);
	}
	
	/**
	 * Function to add a global variable
	 * @param key
	 * @param value
	 */
	public void setGlobal(String key, Object value) {
		globals.put(key, value);
	}
	
	/**
	 * Returns the single experiment instance.
	 */
	static public Experiment get() {
		return instance;
	}
	
	
	
	
	
}
